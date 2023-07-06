package vn.vnpay.service.implement;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dataaccess.FeeCommandDA;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.utils.FeeCommandUtil;

import java.sql.SQLException;
import java.util.Objects;

public class FeeCommandServiceImpl implements FeeCommandService {
    private ValidationService validationService = new ValidationService();
    private FeeCommandUtil feeCommandUtil = new FeeCommandUtil();
    private FeeCommandDA feeCommandDA = new FeeCommandDA();
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    @Override
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException {
        FullHttpResponse response = validationService.validationFeeCommand(createFeeCommandReq);
        if (Objects.nonNull(response))
            return response;
        if (!feeCommandDA.addFeeCommand(createFeeCommandReq)) {
            LOGGER.error("Save fee command to database Fail");
            return feeCommandUtil.createResponse(HttpResponseStatus.BAD_REQUEST, "Bad Request");
        }

        return feeCommandUtil.createResponse(HttpResponseStatus.OK, "Success");
    }

    @Override
    public FullHttpResponse updateFeeTransaction(String pathParam) {

        return feeCommandUtil.createResponse(HttpResponseStatus.OK, "Success");
    }
}
