package vn.vnpay.service;

import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.dto.CreateFeeRequest;

public interface FeeService {
    public FullHttpResponse createFee(CreateFeeRequest createFeeRequest);
    public FullHttpResponse getListFee();
}
