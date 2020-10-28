# 学习笔记

[TOC]

## 第三课作业实践
### 作业一：使用 GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。

以下为使用不同GC算法情况下，分别统计出GC在不同堆内存大小下的GC表现，以单位时间内（1S、5S）生成对象数量为参照，每种命令使用脚本跑10次取平均值，对最终结果进行分析。不同GC执行命令如下：

```java
java -XX:+UseSerialGC -Xms128m -Xmx128m  -XX:+PrintGCDetails GCLogAnalysis
java -XX:+UseParallelGC -Xms128m -Xmx128m  -XX:+PrintGCDetails GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms128m -Xmx128m  -XX:+PrintGCDetails GCLogAnalysis
java -XX:+UseG1GC  -Xms128m -Xmx128m  -XX:+PrintGCDetails GCLogAnalysis
```

用例生成对象时长为1S时执行结果如下：

| GC/内存大小 | 128M | 256M   | 512M   | 1g     | 2g     | 4g     | 8g     |
| ----------- | ---- | ------ | ------ | ------ | ------ | ------ | ------ |
| Serial GC   | OOM  | OOM    | 8472.1 | 9338.6 | 8459.9 | 8001.7 | 5795.7 |
| Parallel GC | OOM  | OOM    | 8007.0 | 9022.7 | 9788.9 | 7595.3 | 5997.7 |
| CMS GC      | OOM  | 4404.7 | 8945.9 | 8156.3 | 8974.7 | 8489.2 | 7904.3 |
| G1 GC       | OOM  | OOM    | 9747.7 | 9172.2 | 9022.5 | 8822.8 | 9235.2 |

对于1S时长的来说，除了能看到256M->512M内存增大之后，CMS有明显的变化，后面增大内存对对象生产性能影响不大，初步猜测是因为执行时间过短，而不同内存下，对于不同的GC来说，执行垃圾回收的时长相差无几，故性能上相差不多，只有521M情况下，G1表现优越。

用例生成对象时长为5S时执行结果如下：

| GC/内存大小 | 128M | 256M | 512M    | 1g      | 2g      | 4g      | 8g      |
| ----------- | ---- | ---- | ------- | ------- | ------- | ------- | ------- |
| Serial GC   | OOM  | OOM  | 34654.9 | 64204.3 | 73469.4 | 68404.3 | 74952.1 |
| Parallel GC | OOM  | OOM  | 21134.4 | 64536.0 | 68017.9 | 71351.1 | 83451.1 |
| CMS GC      | OOM  | OOM  | 34208.3 | 74091.5 | 70214.3 | 64224.1 | 56322.3 |
| G1 GC       | OOM  | OOM  | 29518.2 | 67566.7 | 67518.0 | 68515.6 | 64239.1 |

从上面两种不同生成对象时长的测试结果来看，从128->256->512-1g 的内存增长过程中，性能有明显的提升，从最初的OOM，到对象生成数量稳定于67000~73000之间。

得出结论如下：

- 串行GC随内存增大性能提升，超过2g后性能表现下降。

- 并行GC随着内存增大，处于性能提升显著（所以也可以反映出为什么java8选择使用并行GC作为默认GC）且相对稳定，只有在1S的测试中，8g内存情况下出现断崖式下跌
- CMS GC 随着内存提升，性能提升显著，但在内存超过2g后，同样出现下跌的情况
- G1 GC 相对稳定，除了在1g之间内存的增加对性能提升比较明显，后面随着内存增大，性能趋于稳定

从GC原理出发来看，假设处理器性能一直处于稳定状态，内存较小时，虽然碎片收集的频繁，但是总体需要收集的内存较少，所以内存的清理回收过程较短；而随着内存增大， 发生GC和Full GC的次数减少了，但是回收过程中需要标记和清理的时间增加了，并且该过程中出现了STW，占用了系统资源用来回收垃圾，所以在性能上升到一定瓶颈之后，会转而下降。

单从1S的测试来看，G1是要优于其他收集器的，个人猜测是因为每次G1收集的时候并不是全部收集，只回收部分块，这样就相当于在大内存上，发生的GC次数少，且每次GC回收的垃圾也少，相对来说比其他收集器优越。但是应用程序从来都不是只跑1S的，拉长运行时间来看，G1的优势并不是最高的性能，而是最稳定的性能，无论多大的内存（堆内存大于等于1G），都能保持一个稳定的性能。



#### GC日志分析：

这一块不属于作业内容范畴，自己课余分析日志记录使用



### 作业二：使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar 示例。 

压测使用wrk，线程数20，连接数100，压测持续时间30s。压测命令如下：

```java
wrk -t20 -c100 -d30s --latency http://localhost:8088/api/hello
```

在不同内存下执行测试用例，运行命令如下：

```java
 java -XX:+UseSerialGC -Xms256m -Xmx256m -jar gateway-server-0.0.1-SNAPSHOT.jar
 java -XX:+UseParallelGC -Xms256m -Xmx256m -jar gateway-server-0.0.1-SNAPSHOT.jar
 java -XX:+UseConcMarkSweepGC -Xms256m -Xmx256m -jar gateway-server-0.0.1-SNAPSHOT.jar
 java -XX:+UseG1GC -Xms256m -Xmx256m -jar gateway-server-0.0.1-SNAPSHOT.jar
```

压测结果如下：

- 串行 GC

| 内存大小     | 256m     | 512m     | 1g       | 2g       | 4g       | 8g       |
| ------------ | -------- | -------- | -------- | -------- | -------- | -------- |
| 50%          | 4.12ms   | 4.51ms   | 3.18ms   | 4.59ms   | 4.81ms   | 4.13ms   |
| 75%          | 13.72ms  | 10.79ms  | 9.22ms   | 17.80ms  | 15.53ms  | 23.53ms  |
| 90%          | 99.81ms  | 81.61ms  | 85.64ms  | 106.76ms | 98.15ms  | 130.57ms |
| 99%          | 280.45ms | 256.11ms | 277.84ms | 351.56ms | 270.29ms | 345.70ms |
| Avg          | 23.88ms  | 27.86ms  | 24.30ms  | 32.41ms  | 27.65ms  | 36.17ms  |
| Max          | 657.68ms | 787.39ms | 619.82ms | 880.37ms | 633.74ms | 837.74ms |
| TPS          | 18262.98 | 18050.74 | 22995.05 | 16418.32 | 16206.37 | 16708.40 |
| Transfer/sec | 2.16MB   | 2.18MB   | 2.75MB   | 1.96MB   | 1.93MB   | 1.99MB   |

- 并行 GC

| 内存大小     | 256m     | 512m     | 1g       | 2g       | 4g       | 8g       |
| ------------ | -------- | -------- | -------- | -------- | -------- | -------- |
| 50%          | 5.53ms   | 5.07ms   | 4.39ms   | 2.85ms   | 3.80ms   | 3.65ms   |
| 75%          | 21.91ms  | 9.42ms   | 12.19ms  | 6.42ms   | 8.10ms   | 7.71ms   |
| 90%          | 109.34ms | 38.05ms  | 92.73ms  | 66.72ms  | 70.86ms  | 54.53ms  |
| 99%          | 301.17ms | 210.30ms | 267.72ms | 224.60ms | 249.19ms | 231.88ms |
| Avg          | 31.92ms  | 16.92ms  | 26.11ms  | 19.44ms  | 21.56ms  | 18.88ms  |
| Max          | 738.65ms | 472.66ms | 620.11ms | 595.78ms | 706.91ms | 634.05ms |
| TPS          | 13565.80 | 16641.77 | 17702.98 | 26691.98 | 21244.96 | 21601.22 |
| Transfer/sec | 1.62MB   | 1.99MB   | 2.11MB   | 3.19MB   | 2.54MB   | 2.58MB   |

- CMS GC

| 内存大小     | 256m     | 512m     | 1g       | 2g       | 4g       | 8g       |
| ------------ | -------- | -------- | -------- | -------- | -------- | -------- |
| 50%          | 4.34ms   | 4.50ms   | 4.00ms   | 3.75ms   | 4.93ms   | 4.21ms   |
| 75%          | 17.48ms  | 17.52ms  | 11.27ms  | 9.36ms   | 9.07ms   | 12.75ms  |
| 90%          | 117.13ms | 105.92ms | 76.37ms  | 82.19ms  | 45.70ms  | 86.31ms  |
| 99%          | 329.13ms | 304.79ms | 249.76ms | 268.03ms | 217.38ms | 267.43ms |
| Avg          | 32.77ms  | 30.35ms  | 23.03ms  | 23.88ms  | 17.81ms  | 25.50ms  |
| Max          | 642.74ms | 696.76ms | 545.56ms | 643.68ms | 553.16ms | 616.28ms |
| TPS          | 17343.04 | 16497.02 | 19066.42 | 20501.94 | 16858.96 | 18079.47 |
| Transfer/sec | 2.07MB   | 1.97MB   | 2.28MB   | 2.45MB   | 2.01MB   | 2.16MB   |

- G1 GC

| 内存大小     | 256m     | 512m     | 1g       | 2g       | 4g       | 8g       |
| ------------ | -------- | -------- | -------- | -------- | -------- | -------- |
| 50%          | 4.42ms   | 4.18ms   | 4.32ms   | 4.00ms   | 3.94ms   | 5.01ms   |
| 75%          | 13.17ms  | 8.52ms   | 10.22ms  | 14.18ms  | 8.95ms   | 23.07ms  |
| 90%          | 101.48ms | 55.96ms  | 72.63ms  | 106.43ms | 83.75ms  | 114.55ms |
| 99%          | 301.00ms | 279.01ms | 263.25ms | 347.25ms | 279.72ms | 282.92ms |
| Avg          | 28.86ms  | 21.19ms  | 22.96ms  | 31.01ms  | 24.54ms  | 31.70ms  |
| Max          | 755.46ms | 863.72ms | 637.67ms | 676.55ms | 648.43ms | 537.89ms |
| TPS          | 17473.90 | 19185.71 | 18572.15 | 18448.57 | 20297.74 | 17152.84 |
| Transfer/sec | 2.09MB   | 2.29MB   | 2.22MB   | 2.20MB   | 2.42MB   | 2.05MB   |



不同GC在不同内存下的表现如上四个表格所示，通过对比观察可以看到，各GC随着内存增大，TPS都有所下降，其中变化最明显的是串行GC，这个结果也与预想的相吻合，并行GC一如既往的善变，有最高性能的表现，也有最低性能的表现，总体来说，内存大于1g后性能表现很出色。CMS GC相对稳定，内存增大到2g以上性能有所下降。G1 GC总体来说与CMS的相差不大，很稳定，对此我有一些疑问，觉得G1 应该有更好的效果，之后对G1 进行了反复测试。发现只有在程序初次启动之时进行的压测性能不理想，在经过“预热”之后，性能整体翻倍。这个具体原因还需要另外查询。

### 作业三：(选做)如果自己本地有可以运行的项目，可以按照2的方式进行演练。

自己练习，并分析GC日志。

## 第四课作业实践

### 作业一：(可选)运行课上的例子，以及Netty的例子，分析相关现象。

使用wrk压测 几个Socket例子，发现压测完之后没有结果，应该是应为Socket服务端关闭连接有关每次调用响应结果是connection reset by peer 。为了验证wrk没有问题，贴出压测baidu时的结果

```
➜  ~ wrk -t20 -c100 -d30s --latency http://www.baidu.com
Running 30s test @ http://www.baidu.com
  20 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    72.38ms  156.58ms   1.92s    94.12%
    Req/Sec   121.89     35.32   282.00     86.25%
  Latency Distribution
     50%   36.27ms
     75%   38.40ms
     90%   47.68ms
     99%  932.64ms
  48000 requests in 30.10s, 712.96MB read
  Socket errors: connect 0, read 3566, write 0, timeout 6
Requests/sec:   1594.84
Transfer/sec:     23.69MB
```



压测时长为30S，压测结果：

| Socket Type/Thread Nums | 10       | 20       | 40   |
| ----------------------- | -------- | -------- | ---- |
| 单线程Socket            |          |          |      |
| 多线程                  |          |          |      |
| 线程池                  |          |          |      |
| Netty                   | 73083.92 | 75202.04 |      |



### 作业二：写一段代码，使用HttpClient或OkHttp访问 http://localhost:8801，代码提交到 github。

 HttpClient项目中常用的，OkHttp倒是没用过，所以本次编码使用OkHttp，需要添加maven依赖：

```java
     <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.9.0</version>
        </dependency>
```



代码如下：

```java
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
```

执行发现报了异常：

```
Exception in thread "main" java.lang.NoSuchMethodError: kotlin.collections.ArraysKt.copyInto([B[BIII)[B
```

嗯，少了包，又添加了一下kotlin-stdlib的依赖

```java
  <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>1.4.10</version>
        </dependency>
```

再次执行，ok，正常输出了，输出结果如下：

```java
hello,kimmking

Process finished with exit code 0
```

暂时没有深入了解OkHttp的使用。后续自行了解了。