package com.github.brucema.netty.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @InterfaceName HttpRequestFilter
 * @Author: Created By bruceMa
 * @Description 自定义过滤器接口
 * @Date: 2020/11/4 9:12 下午
 * @Version 1.0
 */
public interface Filter {
    /**
     * 自定义过滤器 网关业务中，接收请求数据时，对请求数据进行预处理
     * 验证签名等
     *
     * @param httpRequest
     * @param ctx
     */
    void filter(FullHttpRequest httpRequest, ChannelHandlerContext ctx);

    /**
     * 自定义后置处理器 ，响应信息中，对响应信息进行加密 加签等操作
     *
     * @param httpResponse
     * @param ctx
     */
    void filter(FullHttpResponse httpResponse, ChannelHandlerContext ctx);
}
