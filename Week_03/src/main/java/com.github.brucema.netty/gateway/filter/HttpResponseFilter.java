package com.github.brucema.netty.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @ClassName HttpResponseFilter
 * @Author: Created By bruceMa
 * @Description 后置过滤器
 * @Date: 2020/11/4 9:26 下午
 * @Version 1.0
 */
public class HttpResponseFilter implements Filter {

    @Override
    public void filter(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {

    }

    @Override
    public void filter(FullHttpResponse httpResponse, ChannelHandlerContext ctx) {

    }
}
