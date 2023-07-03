package vn.vnpay.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.Module;
import vn.vnpay.controller.FeeController;

public class NettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private static final int PORT = 8082;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpRequestDecoder());
                            p.addLast(new HttpResponseEncoder());
                            p.addLast(new HttpObjectAggregator(64 * 1024));

                            Injector injector = Guice.createInjector(new Module());
                            FeeController feeController = injector.getInstance(FeeController.class);

                            p.addLast(feeController);
                        }
                    });

            ChannelFuture f = b.bind(PORT).sync();
            LOGGER.info("Server started and listening on port " + PORT);

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
