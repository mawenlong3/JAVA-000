学习笔记

作业：

作业一： 使用Netty搭建网关，将请求固定返回的字符串，改为调用后端服务，返回后端服务的响应信息

见代码部分



作业二：使用Netty替换掉HTTP客户端来访问后端服务（选做）

由于时间问题，该部分功能尚有bug，待调试



作业三：实现request请求的过滤器

自定义拦截器，分为请求拦截器和响应拦截器，响应拦截器尚未实现，请求拦截器中实现了简单功能，修改请求头信息，实现代码如下：

        @Override
        public void filter(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {
            HttpHeaders headers = httpRequest.headers();
    //        for (Map.Entry<String, String> entry : headers.entries()) {
    //            System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
    //        }
            headers.add("NIO", "Dear BruceMa,This is Request Filter");
        }

作业四：增加路由功能，从代理单个后端服务，改为代理多个后端服务，并根据路由算法去选择调用后端服务（选做）

该部分功能待完成。



---

Netty学习总结

待完善

