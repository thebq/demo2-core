package vn.vnpay.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.model.Result;
import vn.vnpay.util.FeeCommandUtil;

import java.util.Objects;

public class ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private FeeCommandUtil feeCommandUtil = new FeeCommandUtil();
    private ObjectMapper objectMapper = new ObjectMapper();

    public FullHttpResponse validationFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws JsonProcessingException {
        FullHttpResponse response = null;
        if (Objects.isNull(createFeeCommandReq)) {
            LOGGER.info("Body request is missing");
            Result result = new Result(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                    "Body request is missing", null);
            response = feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, result.toString());
        }
        return response;
    }
}
