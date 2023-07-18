package vn.vnpay.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dataaccess.FeeCommandDA;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.CreateFeeTransactionReq;
import vn.vnpay.enums.FeeCommandStatus;
import vn.vnpay.model.FeeCommand;
import vn.vnpay.model.FeeTransaction;
import vn.vnpay.model.Result;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.util.FeeCommandUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FeeCommandServiceImpl implements FeeCommandService {
    private final ValidationService validationService;
    private final FeeCommandUtil feeCommandUtil;
    private final FeeCommandDA feeCommandDA;
    private final RedisService redisService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    public FeeCommandServiceImpl(ValidationService validationService, FeeCommandUtil feeCommandUtil,
                                 FeeCommandDA feeCommandDA, RedisService redisService) {
        this.validationService = validationService;
        this.feeCommandUtil = feeCommandUtil;
        this.feeCommandDA = feeCommandDA;
        this.redisService = redisService;
    }

    @Override
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException, JsonProcessingException {
        LOGGER.info("START create fee command code: {}", createFeeCommandReq.getCommandCode());
        // TODO: add validate
        FullHttpResponse response = validationService.validationFeeCommand(createFeeCommandReq);

        if (Objects.nonNull(response))
            return response;

        if (!checkRequest(createFeeCommandReq.getRequestId(), createFeeCommandReq.getRequestTime())) {
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Request Id exist or Request time expire", null);
            return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
        } else {
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            redisService.setValueToRedis(String.format("%s%s", date, createFeeCommandReq.getRequestId()), createFeeCommandReq.getRequestId());
        }
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
            createFeeTransactionReq.setFeeAmount(createFeeCommandReq.getTotalFee());
            createFeeTransactionReq.setCreateDate(createFeeCommandReq.getCreatedDate());
            createFeeTransactionReq.setModifiedDate(createFeeCommandReq.getCreatedDate());
            createFeeTransactionReq.setStatus(FeeCommandStatus.KHOI_TAO.getCode());
            createFeeTransactionReq.setTotalScan("0");
            createFeeTransactionReq.setTransactionCode(String.valueOf(UUID.randomUUID()));
            createFeeTransactionReq.setAccountNumber("1092991010");

            if (feeCommandDA.addFeeTransaction(createFeeTransactionReq)) {
                feeCommandDA.updateFeeCommand(createFeeTransactionReq.getCommandCode(),
                        totalRecord + 1,
                        totalFee + Integer.parseInt(createFeeTransactionReq.getFeeAmount()));
            } else {
                LOGGER.info("Save fee command to database Fail");
                Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                        "Bad Request", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }
        } else {
            if (!feeCommandDA.addFeeCommand(createFeeCommandReq)) {
                LOGGER.info("Save fee command to database Fail");
                Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                        "Bad Request", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }
        }
        LOGGER.info("FINISH create fee command code: {}", createFeeCommandReq.getCommandCode());

        Result result = new Result(String.valueOf(HttpResponseStatus.OK.code()),
                "Success", null);
        return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
    }

    @Override
    public FullHttpResponse updateFeeTransaction(String pathParam) throws SQLException, JsonProcessingException {
        LOGGER.info("START update fee transaction by command code: {}", pathParam);
        List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByCmdCode(pathParam);
        if (Objects.isNull(feeTransactionList)) {
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Bad Request", null);
            return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
        }
        if (feeTransactionList.isEmpty()) {
            Result result = new Result(String.valueOf(HttpResponseStatus.NO_CONTENT.code()),
                    "No Content", null);
            return feeCommandUtil.createResponse(HttpResponseStatus.NO_CONTENT, result.toString());
        }

        for (FeeTransaction feeTransaction : feeTransactionList) {
            if (Objects.equals(0, feeTransaction.getTotalScan())
                    && FeeCommandStatus.KHOI_TAO.getCode().equalsIgnoreCase(feeTransaction.getStatus())) {
                feeTransaction.setTotalScan(1);
                feeTransaction.setStatus(FeeCommandStatus.THU_PHI.getCode());
                feeCommandDA.updateFeeTransaction(feeTransaction);
            }
        }
        LOGGER.info("FINISH update fee transaction by command code: {}", pathParam);

        Result result = new Result(String.valueOf(HttpResponseStatus.OK.code()),
                "Success", null);
        return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
    }

    public Boolean checkRequest(String requestId, String requestTime) {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        // TODO: move to config
        if (millis - Long.parseLong(requestTime) > 600000 || Long.parseLong(requestTime) - millis > 600000)
            return false;
        return !redisService.checkExist(String.valueOf(date), requestId);
    }
}
