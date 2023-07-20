package vn.vnpay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import vn.vnpay.model.FeeTask;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.util.FeeCommandUtil;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

@ChannelHandler.Sharable
public class FeeCommandController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private FeeCommandService feeCommandService;
    private ObjectMapper objectMapper;
    private FeeCommandUtil feeCommandUtil = new FeeCommandUtil();

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
            L
        }

    }

    private void updateFeeTransaction(ChannelHandlerContext ctx, FullHttpRequest request) throws SQLException, JsonProcessingException {

        FullHttpResponse response = feeCommandService.updateFeeTransaction(request);
        ctx.writeAndFlush(response);
    }


    private void createFeeCommand(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException, SQLException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        CreateFeeCommandReq createFeeCommandReq = objectMapper.readValue(requestBody, CreateFeeCommandReq.class);

        FullHttpResponse response = feeCommandService.createFeeCommand(createFeeCommandReq);
        ctx.writeAndFlush(response);
    }
}
