package com.github.brucema.netty.gateway;

import com.github.brucema.netty.gateway.inbound.HttpInboundServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyServerApplication
 * @Author: Created By bruceMa
 * @Description Netty 启动主类
 * @Date: 2020/11/3 9:39 上午
 * @Version 1.0
 */
public class NettyServerApplication {
    private static Logger logger = LoggerFactory.getLogger(NettyServerApplication.class);
    public static final String GATEWAY_NAME = "NIOGateWay";
    public static final String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {
        String proxyServer = System.getProperty("proxyServer", "http://localhost:8088");
        String proxyPort = System.getProperty("proxyPort","8888");

        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServer);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServer);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println(proxyServer);
    }
}
