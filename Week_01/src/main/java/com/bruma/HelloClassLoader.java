package com.bruma;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * 作业2：
 * 自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节(x=255-x)处理后的文件。
 *
 * @ClassName HelloClassLoader
 * @Author: Created By bruceMa
 * @Description 自定义类加载器
 * @Date: 2020/10/16 6:58 上午
 * @Version 1.0
 */
public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            Class hello = new HelloClassLoader().findClass("Hello");
            Object newInstance = hello.newInstance();
            hello.getMethod("hello").invoke(newInstance);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Hello.xlass")) {
//            使用inputStream中available方法获取字节长度
            byte[] buffer = new byte[inputStream.available()];
            byte[] newBuffer = new byte[inputStream.available()];
            inputStream.read(buffer);
//            原字节文件按照自定义规则进行处理 读取单个字节，转换为新字节，新字节=255-x
            for (int i = 0; i < buffer.length; i++) {
                newBuffer[i] = (byte) (255 - buffer[i]);
            }
            return defineClass(name, newBuffer, 0, newBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
            return super.findClass(name);
        }
    }

}
