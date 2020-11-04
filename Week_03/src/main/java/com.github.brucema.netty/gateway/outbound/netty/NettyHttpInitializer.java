package com.github.brucema.netty.gateway.outbound.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @ClassName NettyHttpinitializer
 * @Author: Created By bruceMa
 * @Description
 * @Date: 2020/11/4 10:04 下午
 * @Version 1.0
 */
public class NettyHttpInitializer extends ChannelInitializer<SocketChannel> {
    private String proxyServer;

    public NettyHttpInitializer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //channel.pipeline().addLast(new HttpResponseDecoder());
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
        ch.pipeline().addLast(new HttpContentDecompressor());
        ch.pipeline().addLast(new NettyInboundHandler(proxyServer));

//          .addLast(new HttpRequestDecoder())
//                .addLast(new HttpResponseDecoder())
//                .addLast(new NettyInboundHandler(proxyServer));
    }
}
