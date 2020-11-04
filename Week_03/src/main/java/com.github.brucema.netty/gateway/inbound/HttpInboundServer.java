package com.github.brucema.netty.gateway.inbound;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName HttpInboundServer
 * @Author: Created By bruceMa
 * @Description 自定义请求报文
 * @Date: 2020/11/3 10:15 下午
 * @Version 1.0
 */
public class HttpInboundServer {
    private static Logger logger = LoggerFactory.getLogger(HttpInboundServer.class);
    private int port;
    private String proxyServer;

    public HttpInboundServer(int port, String proxyServer) {
        this.port = port;
        this.proxyServer = proxyServer;

    }

    public void run() throws InterruptedException {
//        单线程，服务端一个BOSS入口接收请求数据
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(8);
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
//        设置服务端监听队列大小
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
//                       允许重复使用服务端本地地址和端口
                           .option(ChannelOption.SO_REUSEADDR, true)
//                       测试连接状态，长时间无通信时，会自动发送心跳报文
                           .option(ChannelOption.SO_KEEPALIVE, true)
//                       接收缓冲区
                           .option(ChannelOption.SO_RCVBUF, 32 * 1024)
//                       发送缓冲区
                           .option(ChannelOption.SO_SNDBUF, 32 * 1024)
//                       与Nagle算法有关，该算法将小包组装成更大的帧进行发送，可以有效的降低网络负载（多个包组装之后只有一个报文头）,
//                       但是因为要等待小包进行组装，会造成网络延时
//                       设置为true时就是禁用该算法
                           .option(ChannelOption.TCP_NODELAY, true)
//                       连接超时毫秒数 默认30000ms=30s
                           .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
//                       单线程执行ChannelPipeline中的事件，整个pipeline由一个线程执行，这样不需要进行线程切换以及线程同步,默认值为True
//                       如果为False，ChannelHandler中的处理过程会由Group中的不同线程执行。
                           .option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, true)
//                       允许重复使用服务端监听端口
                           .option(EpollChannelOption.SO_REUSEPORT, true)
//                       设置子任务 监听连接状态
                           .childOption(ChannelOption.SO_KEEPALIVE, true)
//                      Netty4 使用，重用缓冲区，即开辟一块内存空间之后，重复使用该部分内存
                           .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 创建netty引导类，配置和串联系列组件（设置线程模型，设置通道类型，设置客户端处理器handler，设置绑定端口号）
            serverBootstrap.group(bossGroup, workGroup)
                           .channel(NioServerSocketChannel.class)
                           .handler(new LoggingHandler(LogLevel.INFO))
                           .childHandler(new HttpInboundInitializer(this.proxyServer));
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            Channel channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
