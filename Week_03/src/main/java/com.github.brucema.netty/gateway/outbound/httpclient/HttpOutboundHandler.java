package com.github.brucema.netty.gateway.outbound.httpclient;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName HttpOutboundHandler
 * @Author: Created By bruceMa
 * @Description 响应信息处理器
 * @Date: 2020/11/4 7:33 上午
 * @Version 1.0
 */
public class HttpOutboundHandler {
    private String backendUrl;
    private ExecutorService executorService;
    private CloseableHttpAsyncClient httpClient;

    public HttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
//        自定义线程池
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        executorService = new ThreadPoolExecutor(cores, cores, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize)
                , new NamedThreadFactory("proxyService"), handler);

//        自定义http连接参数
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                                                         .setConnectTimeout(1000)
                                                         .setSoTimeout(1000)
                                                         .setIoThreadCount(cores)
                                                         .setRcvBufSize(32 * 1024)
                                                         .build();

        httpClient = HttpAsyncClients.custom()
                                     .setMaxConnTotal(40)
                                     .setMaxConnPerRoute(8)
                                     .setDefaultIOReactorConfig(ioReactorConfig)
                                     .setKeepAliveStrategy(((httpResponse, httpContext) -> 6000))
                                     .build();
        httpClient.start();
    }

    public void handler(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        String url = this.backendUrl + fullHttpRequest.uri();
        executorService.submit(() -> fetchGet(fullHttpRequest, ctx, url));
    }

    public void fetchGet(FullHttpRequest request, ChannelHandlerContext ctx, String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpHeaders headers = request.headers();
        for (Map.Entry<String, String> entry : headers.entries()) {
//            System.out.println("请求后端服务添加报文头：" + "【KEY】: " + entry.getKey() + " 【VALUE】: " + entry.getValue());
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpClient.execute(httpGet, new FutureCallback<HttpResponse>() {
            //            请求成功后，对异步响应的后端请求报文处理
            @Override
            public void completed(HttpResponse httpResponse) {
                handleResponse(request, ctx, httpResponse);
            }

            @Override
            public void failed(Exception e) {
                httpGet.abort();
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(FullHttpRequest request, ChannelHandlerContext ctx, HttpResponse httpResponse) {
        FullHttpResponse response = null;
        try {
//        响应信息 转成字节数组
            byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
//            正常响应情况下，将后端返回的响应信息加上报文头信息 返回给客户端
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
//        设置响应报文类型
            response.headers().set("Content-Type", "application/json");
//        设置响应报文长度
            response.headers().setInt("Content-Length", Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue()));
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        } finally {
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    private void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
