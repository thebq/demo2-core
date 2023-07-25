package vn.vnpay.demo2.service.implement;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.model.Result;
import vn.vnpay.demo2.util.LocalProperties;
import vn.vnpay.demo2.util.FeeCommandUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private final FeeCommandUtil feeCommandUtil = new FeeCommandUtil();
    private final RedisService redisService = new RedisService();

    public FullHttpResponse validationFeeCommand(CreateFeeCommandReq createFeeCommandReq) {
        FullHttpResponse response = null;
        if (Objects.isNull(createFeeCommandReq)) {
            LOGGER.info("Body request is missing");
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Body request is missing", null);
            response = feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
        }
        return response;
    }

    public Boolean checkRequestIdExist(String requestId) {
        LocalDateTime dateTime = LocalDateTime.now();
        if (redisService.checkExist(String.valueOf(dateTime), requestId)) {
            LOGGER.info("Request Id exist: {}", requestId);
            return true;
        }
        return false;
    }

    public Boolean checkRequestTimeExpire(String requestTime) {
        long millis = System.currentTimeMillis();
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(requestTime,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            long request = localDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
            if (millis - request > Long.parseLong(LocalProperties.get("time-expire").toString())
                    || request - millis > Long.parseLong(LocalProperties.get("time-expire").toString())) {
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
