package vn.vnpay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.utils.FeeCommandUtil;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        HttpMethod method = request.method();
        String uri = request.uri();
        String[] uriSplit = uri.split("/");
        String pathParam = uriSplit[uriSplit.length-1];

        if (HttpMethod.PUT.equals(method)) {
            String path = "/fee/" + pathParam;
            if (path.equals(uri)) {
                updateFeeTransaction(ctx, pathParam);
            }
        } else if (HttpMethod.POST.equals(method)) {
            if ("/fee/create".equals(uri)) {
                handleCreateFee(ctx, request);
            }
        }
    }

    private void updateFeeTransaction(ChannelHandlerContext ctx, String pathParam) throws SQLException {

        FullHttpResponse response = feeCommandService.updateFeeTransaction(pathParam);
        ctx.writeAndFlush(response);
    }


    private void handleCreateFee(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException, SQLException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        CreateFeeCommandReq createFeeCommandReq = objectMapper.readValue(requestBody, CreateFeeCommandReq.class);

        FullHttpResponse response = feeCommandService.createFeeCommand(createFeeCommandReq);
        ctx.writeAndFlush(response);
    }
}
