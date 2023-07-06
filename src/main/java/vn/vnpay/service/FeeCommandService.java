package vn.vnpay.service;

import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.dto.CreateFeeCommandReq;

import java.sql.SQLException;

public interface FeeCommandService {
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException;
    public FullHttpResponse updateFeeTransaction(String pathParam) throws SQLException;
}
