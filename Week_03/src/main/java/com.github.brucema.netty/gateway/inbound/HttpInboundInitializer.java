package com.github.brucema.netty.gateway.inbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @ClassName HttpInboundInitialazer
 * @Author: Created By bruceMa
 * @Description
 * @Date: 2020/11/4 7:02 上午
 * @Version 1.0
 */
public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
    private String proxyServer;

    public HttpInboundInitializer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
//        配置链式解码器,HTTP请求就解码成HTTP请求报文，如果为TCP或其他请求，配置对应解码器进行解码
        ChannelPipeline p = ch.pipeline();

//        添加HTTP解码器，将报文解析成HTTPRequest
        p.addLast(new HttpServerCodec())
//         解析成FullHttpRequest
//         当我们用POST方式请求服务器的时候，对应的参数信息是保存在message body中的,
//         如果只是单纯的用HttpServerCodec是无法完全的解析Http POST请求的，因为HttpServerCodec只能获取uri中参数，
//         所以需要加上HttpObjectAggregator。
         .addLast(new HttpObjectAggregator(1024 * 1024))
//         添加自定义处理器
         .addLast(new HttpInboundHandler(this.proxyServer));
    }
}
