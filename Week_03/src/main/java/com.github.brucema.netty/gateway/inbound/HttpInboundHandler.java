package com.github.brucema.netty.gateway.inbound;

import com.github.brucema.netty.gateway.filter.Filter;
import com.github.brucema.netty.gateway.filter.HttpRequestFilter;
import com.github.brucema.netty.gateway.filter.HttpResponseFilter;
import com.github.brucema.netty.gateway.outbound.httpclient.HttpOutboundHandler;
import com.github.brucema.netty.gateway.outbound.netty.NettyHttpClient;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import static org.apache.http.HttpHeaders.CONNECTION;


/**
 * @ClassName HttpInboundHandler
 * @Author: Created By bruceMa
 * @Description 自定义请求处理器
 * @Date: 2020/11/4 7:19 上午
 * @Version 1.0
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private String proxyServer;
    private HttpOutboundHandler httpOutboundHandler;
    private NettyHttpClient nettyHttpClient;
    private Filter httpRequestFilter;
    private Filter httpResponseFilter;

    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
        httpOutboundHandler = new HttpOutboundHandler(this.proxyServer);
        httpRequestFilter = new HttpRequestFilter();
        httpResponseFilter = new HttpResponseFilter();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        String uri = fullHttpRequest.uri();
        System.out.println("接收到的请求url为" + uri);
//        原始处理方式 根据路径判断响应信息，后续进行改进
//        if (uri.contains("/test")) {
//            handlerTest(fullHttpRequest, ctx);
//        }

//        前置过滤器
        httpRequestFilter.filter(fullHttpRequest, ctx);

//      按照转发规则执行处理
        httpOutboundHandler.handler(fullHttpRequest, ctx);
    }


    private void handlerTest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
        String content = "Hello,bruceMa";
        try {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        } finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
