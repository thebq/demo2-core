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
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.constant.FeeCommandConstant;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.dto.UpdateFeeCommandReq;
import vn.vnpay.demo2.enums.MetaData;
import vn.vnpay.demo2.model.Result;
import vn.vnpay.demo2.service.FeeCommandService;
import vn.vnpay.demo2.util.FeeCommandUtil;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class FeeCommandController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final FeeCommandService feeCommandService;
    private final ObjectMapper objectMapper;
    private final FeeCommandUtil feeCommandUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandController.class);

    @Inject
    public FeeCommandController(FeeCommandService feeCommandService, FeeCommandUtil feeCommandUtil) {
        this.feeCommandService = feeCommandService;
        this.feeCommandUtil = feeCommandUtil;
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        try {
            if (HttpMethod.POST.equals(method)) {
                if (FeeCommandConstant.UPDATE_URL.equals(uri)) {
                    updateFeeTransaction(ctx, request);
                }
                if (FeeCommandConstant.CREATE_URL.equals(uri)) {
                    createFeeCommand(ctx, request);
                }
                handleRequest(ctx);
            }
        } catch (Exception e) {
            LOGGER.error("Fail to request. {}", e.getMessage());
        }
    }

    private void updateFeeTransaction(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        UpdateFeeCommandReq updateFeeCommandReq = objectMapper.readValue(requestBody, UpdateFeeCommandReq.class);
        LOGGER.info("START request id: {}", updateFeeCommandReq.getRequestId());
        FullHttpResponse response = feeCommandService.updateFeeTransaction(updateFeeCommandReq);
        LOGGER.info("FINISH request id: {}", updateFeeCommandReq.getRequestId());
        ctx.writeAndFlush(response);
    }

    private void createFeeCommand(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        CreateFeeCommandReq createFeeCommandReq = objectMapper.readValue(requestBody, CreateFeeCommandReq.class);
        LOGGER.info("START request id: {}", createFeeCommandReq.getRequestId());
        FullHttpResponse response = feeCommandService.createFeeCommand(createFeeCommandReq);
        LOGGER.info("FINISH request id: {}", createFeeCommandReq.getRequestId());
        ctx.writeAndFlush(response);
    }

    private void handleRequest(ChannelHandlerContext ctx) {
        Result result = new Result(String.valueOf(MetaData.NOT_FOUND.getMetaCode()),
                MetaData.NOT_FOUND.getMessage(), null);
        FullHttpResponse response =  feeCommandUtil.createResponse(HttpResponseStatus.OK, result.toString());
        ctx.writeAndFlush(response);
    }
}
