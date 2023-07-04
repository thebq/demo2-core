package vn.vnpay.service;

import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.dto.CreateFeeCommandReq;

public interface FeeCommandService {
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq);
    public FullHttpResponse getListFeeCommand();
}
