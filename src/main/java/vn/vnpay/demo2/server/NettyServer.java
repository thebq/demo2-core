package vn.vnpay.demo2.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.config.Module;
import vn.vnpay.demo2.controller.FeeCommandController;
import vn.vnpay.demo2.util.LocalProperties;

import java.io.IOException;

/**
 * @author thebq
 * Created: 03/08/2023
 */
public class NettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private static int PORT;

    static {
        try {
            PORT = Integer.parseInt(String.valueOf(LocalProperties.get("netty-port")));
        } catch (IOException e) {
            LOGGER.error("Load config FAIL");
        }
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 255)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpRequestDecoder());
                            p.addLast(new HttpResponseEncoder());
                            p.addLast(new HttpObjectAggregator(64 * 1024));

                            Injector injector = Guice.createInjector(new Module());
                            FeeCommandController feeCommandController = injector.getInstance(FeeCommandController.class);

                            p.addLast(feeCommandController);
                        }
                    });

            ChannelFuture f = b.bind(PORT).sync();
            LOGGER.info("Server started on port: " + PORT);

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Start server FAIL");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
