package vn.vnpay.service.implement;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.utils.FeeCommandUtil;

import java.util.Objects;

public class ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private FeeCommandUtil feeCommandUtil = new FeeCommandUtil();

    public FullHttpResponse validationFeeCommand(CreateFeeCommandReq createFeeCommandReq) {
        FullHttpResponse response = null;
        if (Objects.isNull(createFeeCommandReq)) {
            LOGGER.warn("Body request is missing");
            response = feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, "Body request is missing");
        }
        return response;
    }
}
