package com.github.brucema.netty.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * @ClassName HttpRequestFilterImpl
 * @Author: Created By bruceMa
 * @Description 过滤器
 * @Date: 2020/11/4 9:14 下午
 * @Version 1.0
 */
public class HttpRequestFilter implements Filter {

    @Override
    public void filter(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = httpRequest.headers();
//        for (Map.Entry<String, String> entry : headers.entries()) {
//            System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
//        }
        headers.add("NIO", "Dear BruceMa,This is Request Filter");
    }

    @Override
    public  void filter(FullHttpResponse httpResponse, ChannelHandlerContext ctx) {

    }
}
