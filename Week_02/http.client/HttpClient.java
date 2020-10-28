package io.github.kimmking.netty.server;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * OkHttp 进行调用
 * @ClassName HttpClient
 * @Author: Created By bruceMa
 * @Description
 * @Date: 2020/10/28 8:32 上午
 * @Version 1.0
 */
public class HttpClient {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
//        创建get请求，
        Request request = new Request.Builder().get().url("http://localhost:8808/test").build();
        try {
//        同步响应结果
            Response execute = client.newCall(request).execute();
//            打印响应信息
            System.out.println(execute.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
