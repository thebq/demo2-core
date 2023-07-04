package vn.vnpay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import vn.vnpay.dto.CreateFeeRequest;
import vn.vnpay.service.FeeService;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class FeeController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private FeeService feeService;
    private ObjectMapper objectMapper;

    @Inject
    public FeeController(FeeService feeService) {
        this.feeService = feeService;
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        HttpMethod method = request.method();
        String uri = request.uri();

        if(HttpMethod.GET.equals(method)){
            if("/fee".equals(uri)){
                listAllFeeCommand(ctx);
            }
        }
        else if (HttpMethod.POST.equals(method)) {
            if ("/fee/create".equals(uri)) {
                handleCreateFee(ctx, request);
            }
        }


    }

    private void listAllFeeCommand(ChannelHandlerContext ctx) throws JsonProcessingException {
//        List<> feeCommands = feeService.getListFee();

        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = "objectMapper.writeValueAsString(feeCommands)";

        FullHttpResponse response = createResponse(HttpResponseStatus.OK, jsonResponse);
        ctx.writeAndFlush(response);
    }


    private void handleCreateFee(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);

        CreateFeeRequest createFeeRequest =  objectMapper.readValue(requestBody,CreateFeeRequest.class);
        feeService.createFee(createFeeRequest);

        FullHttpResponse response = createResponse(HttpResponseStatus.OK, "Create fee success");
        ctx.writeAndFlush(response);
    }

    private FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
