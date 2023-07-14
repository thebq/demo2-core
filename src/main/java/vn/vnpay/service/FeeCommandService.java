package vn.vnpay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.dto.CreateFeeCommandReq;

import java.sql.SQLException;

public interface FeeCommandService {
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException, JsonProcessingException;
    public FullHttpResponse updateFeeTransaction(String pathParam) throws SQLException, JsonProcessingException;
}
