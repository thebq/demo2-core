package vn.vnpay.controller;

import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import vn.vnpay.service.FeeService;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class FeeController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private FeeService feeService;

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
            if("/fee-command".equals(uri)){
                listAllFeeCommand(ctx);
            }
        }
        else if (HttpMethod.POST.equals(method)) {
            if ("/fee-command/add".equals(uri)) {
                handleAddFeeCommand(ctx, request);
            }
        }


    }

    private void listAllFeeCommand(ChannelHandlerContext ctx) throws JsonProcessingException {
        List<FeeCommandSearchInfo> feeCommands = feeCommandService.listAllFeeCommand();
        // Sử dụng Jackson để chuyển đổi danh sách thành chuỗi JSON
        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = objectMapper.writeValueAsString(feeCommands);

        // Tạo và gửi response với dữ liệu JSON
        FullHttpResponse response = createResponse(HttpResponseStatus.OK, jsonResponse);
        ctx.writeAndFlush(response);
    }


    private void handleAddFeeCommand(ChannelHandlerContext ctx, FullHttpRequest request) throws JsonProcessingException {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);

        FeeCommandAddInfo feeCommandAddInfo =  objectMapper.readValue(requestBody,FeeCommandAddInfo.class);
        feeCommandService.addFeeCommand(feeCommandAddInfo);

        FullHttpResponse response = createResponse(HttpResponseStatus.CREATED, "FeeCommand created");
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
