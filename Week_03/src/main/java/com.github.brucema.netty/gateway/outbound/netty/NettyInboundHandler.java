package com.github.brucema.netty.gateway.outbound.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @ClassName HttpClientOutboundHandler
 * @Author: Created By bruceMa
 * @Description
 * @Date: 2020/11/4 10:07 下午
 * @Version 1.0
 */
public class NettyInboundHandler extends ChannelInboundHandlerAdapter {
    private String backendUrl;

    public NettyInboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        URI uri = new URI(backendUrl);
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse)msg;
        ByteBuf buf = response.content();
        String result = buf.toString(CharsetUtil.UTF_8);
        System.out.println("response -> "+result);

//        FullHttpResponse response = (FullHttpResponse) msg;
//        HttpHeaders headers = response.headers();
//        for (Map.Entry<String, String> entry : headers.entries()) {
//            System.out.println("请求后端服务添加报文头：" + "【KEY】: " + entry.getKey() + " 【VALUE】: " + entry.getValue());
//        }
//        try {
////        响应信息 转成字节数组
//            byte[] body = response.content().array();
////            正常响应情况下，将后端返回的响应信息加上报文头信息 返回给客户端
//            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
////        设置响应报文类型
//            response.headers().set("Content-Type", "application/json");
////        设置响应报文长度
//            response.headers().setInt("Content-Length", Integer.parseInt(headers.get("Content-Length")));
//        } catch (Exception e) {
//            e.printStackTrace();
//            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
//        } finally {
//            ctx.writeAndFlush(response);
//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("客户端读取数据完毕");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("client 读取数据出现异常");
        ctx.close();
    }
}
