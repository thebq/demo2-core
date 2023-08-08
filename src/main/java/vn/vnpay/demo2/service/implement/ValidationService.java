package vn.vnpay.demo2.service.implement;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.constant.FeeCommandConstant;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.enums.MetaData;
import vn.vnpay.demo2.model.Result;
import vn.vnpay.demo2.util.FeeCommandUtil;
import vn.vnpay.demo2.util.LocalProperties;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author thebq
 * Created: 03/08/2023
 */
public class ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private final FeeCommandUtil feeCommandUtil = new FeeCommandUtil();
    private final RedisService redisService = new RedisService();

    public FullHttpResponse validationFeeCommand(CreateFeeCommandReq createFeeCommandReq) {
        if (Objects.isNull(createFeeCommandReq)) {
            LOGGER.info("Body request is missing");
            Result result = new Result(String.valueOf(MetaData.FAIL_REQUEST.getMetaCode()),
                    MetaData.FAIL_REQUEST.getMessage(), null);
            return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }

        if (Objects.isNull(createFeeCommandReq.getCommandCode())) {
            LOGGER.info("Command code is missing");
            Result result = new Result(String.valueOf(MetaData.FAIL_REQUEST.getMetaCode()),
                    MetaData.FAIL_REQUEST.getMessage(), null);
            return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }

        if (Objects.isNull(createFeeCommandReq.getTotalRecord())) {
            LOGGER.info("Total record is missing");
            Result result = new Result(String.valueOf(MetaData.FAIL_REQUEST.getMetaCode()),
                    MetaData.FAIL_REQUEST.getMessage(), null);
            return feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }
        return null;
    }

    public Boolean checkRequestIdExist(String requestId) {
        if (redisService.checkExist(requestId)) {
            LOGGER.info("Request Id exist: {}", requestId);
            return true;
        }
        return false;
    }

    public Boolean checkRequestTimeExpire(String requestTime) {
        long millis = System.currentTimeMillis();
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(requestTime,
                    DateTimeFormatter.ofPattern(FeeCommandConstant.YYYYMMDDHHMMSS));
            long request = localDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
            if (millis - request > Long.parseLong(LocalProperties.get(FeeCommandConstant.TIME_EXPIRE).toString())
                    || request - millis > Long.parseLong(LocalProperties.get(FeeCommandConstant.TIME_EXPIRE).toString())) {
                LOGGER.info("Request time expire");
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Check request time exception: {}", e.getMessage());
            return true;
        }
        return false;
    }
}
