package vn.vnpay.demo2.util;

import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
/**
 * @author thebq
 * Created: 03/08/2023
 */
public class FeeCommandUtil {
    public FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
