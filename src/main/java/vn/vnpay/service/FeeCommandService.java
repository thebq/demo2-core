package vn.vnpay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.UpdateFeeCommandReq;

import java.sql.SQLException;

public interface FeeCommandService {
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq);
    public FullHttpResponse updateFeeTransaction(UpdateFeeCommandReq updateFeeCommandReq);
}
