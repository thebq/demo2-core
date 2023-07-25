package vn.vnpay.demo2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.dto.UpdateFeeCommandReq;
import vn.vnpay.demo2.service.FeeCommandService;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class FeeCommandController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final FeeCommandService feeCommandService;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandController.class);

    @Inject
    public FeeCommandController(FeeCommandService feeCommandService) {
        this.feeCommandService = feeCommandService;
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        try {
            if (HttpMethod.PUT.equals(method)) {
                String path = "/fee/update/";
                if (path.equals(uri)) {
                    updateFeeTransaction(ctx, request);
                }
            } else if (HttpMethod.POST.equals(method)) {
                if ("/fee/create".equals(uri)) {
                    createFeeCommand(ctx, request);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fail to request. {}", e.getMessage());
        }

    }

    private void updateFeeTransaction(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        UpdateFeeCommandReq updateFeeCommandReq = objectMapper.readValue(requestBody, UpdateFeeCommandReq.class);

        FullHttpResponse response = feeCommandService.updateFeeTransaction(updateFeeCommandReq);
        ctx.writeAndFlush(response);
    }

    private void createFeeCommand(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        CreateFeeCommandReq createFeeCommandReq = objectMapper.readValue(requestBody, CreateFeeCommandReq.class);

        FullHttpResponse response = feeCommandService.createFeeCommand(createFeeCommandReq);
        ctx.writeAndFlush(response);
    }
}
