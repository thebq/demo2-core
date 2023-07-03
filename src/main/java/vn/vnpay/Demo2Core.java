package vn.vnpay;

import vn.vnpay.server.NettyServer;

public class Demo2Core {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}