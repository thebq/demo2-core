package vn.vnpay.demo2.service.implement;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.inject.Inject;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.dao.FeeCommandDA;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.dto.CreateFeeTransactionReq;
import vn.vnpay.demo2.dto.UpdateFeeCommandReq;
import vn.vnpay.demo2.enums.FeeCommandStatus;
import vn.vnpay.demo2.enums.MetaData;
import vn.vnpay.demo2.model.FeeTransaction;
import vn.vnpay.demo2.model.Result;
import vn.vnpay.demo2.service.FeeCommandService;
import vn.vnpay.demo2.util.FeeCommandUtil;

import java.util.List;
import java.util.Objects;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_ALPHABET;
import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_NUMBER_GENERATOR;

public class FeeCommandServiceImpl implements FeeCommandService {
    private final ValidationService validationService;
    private final FeeCommandUtil feeCommandUtil;
    private final FeeCommandDA feeCommandDA;
    private final RedisService redisService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    @Inject
    public FeeCommandServiceImpl(ValidationService validationService, FeeCommandUtil feeCommandUtil, FeeCommandDA feeCommandDA, RedisService redisService) {
        this.validationService = validationService;
        this.feeCommandUtil = feeCommandUtil;
        this.feeCommandDA = feeCommandDA;
        this.redisService = redisService;
    }

    @Override
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) {
        LOGGER.info("START create fee command code: {} , request Id: {}", createFeeCommandReq.getCommandCode(),
                createFeeCommandReq.getRequestId());
        try {
            FullHttpResponse response = validationService.validationFeeCommand(createFeeCommandReq);

            if (Objects.nonNull(response))
                return response;
            if (validationService.checkRequestIdExist(createFeeCommandReq.getRequestId())) {
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        "Request Id exist", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }
            redisService.setValueToRedis(createFeeCommandReq.getRequestId(), createFeeCommandReq.getRequestId());

            if (validationService.checkRequestTimeExpire(createFeeCommandReq.getRequestTime())) {
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        "Request time expire", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }

            createFeeCommandReq.setCommandCode(NanoIdUtils.randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, 11));
            if (!feeCommandDA.addFeeCommand(createFeeCommandReq)) {
                LOGGER.info("Save fee command to database Fail");
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        MetaData.BAD_REQUEST.getMessage(), null);
                return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
            }
            if (Objects.nonNull(createFeeCommandReq.getTransactions())) {
                for (CreateFeeTransactionReq createFeeTransactionReq : createFeeCommandReq.getTransactions()) {
                    createFeeTransactionReq.setTransactionCode(NanoIdUtils.randomNanoId());
                    createFeeTransactionReq.setCommandCode(createFeeCommandReq.getCommandCode());
                    feeCommandDA.addFeeTransaction(createFeeTransactionReq);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fail to request: {}", e.getMessage());
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Fail to request", null);
            return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }

        LOGGER.info("FINISH create fee command code: {}, request Id: {}", createFeeCommandReq.getCommandCode(),
                createFeeCommandReq.getRequestId());

        Result result = new Result(String.valueOf(HttpResponseStatus.OK.code()),
                "Success", null);
        return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
    }

    @Override
    public FullHttpResponse updateFeeTransaction(UpdateFeeCommandReq updateFeeCommandReq) {
        LOGGER.info("START update fee transaction by command code: {}", updateFeeCommandReq.getCommandCode());
        try {
            if (validationService.checkRequestIdExist(updateFeeCommandReq.getRequestId())) {
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        "Request Id exist", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }
            redisService.setValueToRedis(updateFeeCommandReq.getRequestId(), updateFeeCommandReq.getRequestId());

            if (validationService.checkRequestTimeExpire(updateFeeCommandReq.getRequestTime())) {
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        "Request time expire", null);
                return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
            }

            List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByCmdCode(updateFeeCommandReq.getCommandCode());
            if (Objects.isNull(feeTransactionList)) {
                Result result = new Result(String.valueOf(MetaData.BAD_REQUEST.getMetaCode()),
                        MetaData.BAD_REQUEST.getMessage(), null);
                return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
            }
            if (feeTransactionList.isEmpty()) {
                Result result = new Result(String.valueOf(MetaData.NO_CONTENT.getMetaCode()),
                        MetaData.NO_CONTENT.getMessage(), null);
                return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
            }

            for (FeeTransaction feeTransaction : feeTransactionList) {
                if (Objects.equals(0, feeTransaction.getTotalScan())
                        && FeeCommandStatus.KHOI_TAO.getCode().equalsIgnoreCase(feeTransaction.getStatus())) {
                    feeTransaction.setTotalScan(1);
                    feeTransaction.setStatus(FeeCommandStatus.THU_PHI.getCode());
                    feeCommandDA.updateFeeTransaction(feeTransaction);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fail to request: {}", e.getMessage());
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Fail to request", null);
            return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }
        LOGGER.info("FINISH update fee transaction by command code: {}", updateFeeCommandReq.getCommandCode());

        Result result = new Result(String.valueOf(HttpResponseStatus.OK.code()),
                "Success", null);
        return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
    }
}
