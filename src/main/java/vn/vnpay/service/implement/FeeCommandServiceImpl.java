package vn.vnpay.service.implement;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dataaccess.FeeCommandDA;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.CreateFeeTransactionReq;
import vn.vnpay.model.FeeCommand;
import vn.vnpay.model.FeeTransaction;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.utils.FeeCommandUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FeeCommandServiceImpl implements FeeCommandService {
    private ValidationService validationService = new ValidationService();
    private FeeCommandUtil feeCommandUtil = new FeeCommandUtil();
    private FeeCommandDA feeCommandDA = new FeeCommandDA();
    private RedisService redisService = new RedisService();
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    @Override
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException {
        FullHttpResponse response = validationService.validationFeeCommand(createFeeCommandReq);
        if (Objects.nonNull(response))
            return response;
        List<FeeCommand> feeCommandList = feeCommandDA.getAllFeeCommand();

        boolean checkCommandCodeExist = false;
        Integer totalRecord = 0;
        Integer totalFee = 0;
        if (Objects.nonNull(feeCommandList) && !feeCommandList.isEmpty()) {
            for (FeeCommand feeCommand : feeCommandList) {
                if (feeCommand.getCommandCode().equals(createFeeCommandReq.getCommandCode())) {
                    totalRecord = feeCommand.getTotalRecord();
                    totalFee = feeCommand.getTotalFee();
                    checkCommandCodeExist = true;
                    break;
                }
            }
        }
        if (checkCommandCodeExist) {
            CreateFeeTransactionReq createFeeTransactionReq = new CreateFeeTransactionReq();
            createFeeTransactionReq.setCommandCode(createFeeCommandReq.getCommandCode());
            createFeeTransactionReq.setFeeAmount("200");
            createFeeTransactionReq.setCreateDate(createFeeCommandReq.getCreatedDate());
            createFeeTransactionReq.setModifiedDate(createFeeCommandReq.getCreatedDate());
            createFeeTransactionReq.setStatus("01");
            createFeeTransactionReq.setTotalScan("0");
            createFeeTransactionReq.setTransactionCode(String.valueOf(UUID.randomUUID()));
            createFeeTransactionReq.setAccountNumber("1092991010");

            if (feeCommandDA.addFeeTransaction(createFeeTransactionReq)) {
                feeCommandDA.updateFeeCommand(createFeeTransactionReq.getCommandCode(),
                        totalRecord + 1,
                        totalFee + Integer.parseInt(createFeeTransactionReq.getFeeAmount()));
            } else {
                LOGGER.error("Save fee command to database Fail");
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, "Bad Request");
            }
        } else {
            if (!feeCommandDA.addFeeCommand(createFeeCommandReq)) {
                LOGGER.error("Save fee command to database Fail");
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, "Bad Request");
            }
        }

        return feeCommandUtil.createResponse(HttpResponseStatus.OK, "Success");
    }

    @Override
    public FullHttpResponse updateFeeTransaction(String pathParam) throws SQLException {
        List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByCmdCode(pathParam);
        if (Objects.isNull(feeTransactionList))
            return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, "Bad Request");
        if (feeTransactionList.isEmpty())
            return feeCommandUtil.createResponse(HttpResponseStatus.NO_CONTENT, "No Content");

        for (FeeTransaction feeTransaction : feeTransactionList) {
            if (Objects.equals(0, feeTransaction.getTotalScan()) && "01".equalsIgnoreCase(feeTransaction.getStatus())) {
                feeCommandDA.updateFeeTransaction(feeTransaction);
            }
        }

        return feeCommandUtil.createResponse(HttpResponseStatus.OK, "Success");
    }

    public Boolean checkRequest(String requestId, String requestTime) {
        long millis=System.currentTimeMillis();
        Date date = new Date(millis);
        List<String> requestIdList = redisService.getRequestIdByDate(String.valueOf(date));
        if (requestIdList.contains(requestId))
            return false;
        if (millis - Long.parseLong(requestTime) > 600000)
            return false;
        return true;
    }
}
