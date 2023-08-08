package vn.vnpay.demo2.service;

import io.netty.handler.codec.http.FullHttpResponse;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.dto.UpdateFeeCommandReq;
/**
 * @author thebq
 * Created: 03/08/2023
 */
public interface FeeCommandService {
    public FullHttpResponse createFeeCommand(CreateFeeCommandReq createFeeCommandReq);
    public FullHttpResponse updateFeeTransaction(UpdateFeeCommandReq updateFeeCommandReq);
}
