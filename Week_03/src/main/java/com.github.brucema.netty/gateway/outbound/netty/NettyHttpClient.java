package com.github.brucema.netty.gateway.outbound.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @ClassName NettyHttpClient
 * @Author: Created By bruceMa
 * @Description Netty客户端 实现访问后端服务
 * @Date: 2020/11/4 9:59 下午
 * @Version 1.0
 */
public class NettyHttpClient {

    public void connect(String host, int port, String uri) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.option(ChannelOption.SO_KEEPALIVE, true)
             .remoteAddress(new InetSocketAddress(host, port));
            b.group(workerGroup)
             .channel(NioSocketChannel.class)
             .handler(new NettyHttpInitializer(uri));

//            连接服务端
//            ChannelFuture cf = b.connect(host, port).sync();
            ChannelFuture cf = b.connect().sync();
//            发送请求数据
//            cf.channel().writeAndFlush("");
//            关闭通道 当前线程继续执行
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyHttpClient client = new NettyHttpClient();
        client.connect("localhost", 8088, "/api/hello");
    }
}
