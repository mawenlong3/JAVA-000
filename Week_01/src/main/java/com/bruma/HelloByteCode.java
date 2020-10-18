package com.bruma;

/**
 * 作业 第一题：
 * 自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和 for，然后
 * 自己分析一下对应的字节码。
 * 字节码
 *
 * Last modified 2020-10-15; size 771 bytes
 * MD5 checksum a97ff15cf987e51bec58b8c9311b5e1c
 * Compiled from "HelloByteCode.java"
 * public class com.bruma.HelloByteCode
 * minor version: 0
 * major version: 52
 * flags: ACC_PUBLIC, ACC_SUPER
 * Constant pool:
 * #1 = Methodref          #11.#22        // java/lang/Object."<init>":()V
 * #2 = Fieldref           #23.#24        // java/lang/System.out:Ljava/io/PrintStream;
 * #3 = Class              #25            // java/lang/StringBuilder
 * #4 = Methodref          #3.#22         // java/lang/StringBuilder."<init>":()V
 * #5 = Methodref          #3.#26         // java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
 * #6 = String             #27            // 为偶数
 * #7 = Methodref          #3.#28         // java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
 * #8 = Methodref          #3.#29         // java/lang/StringBuilder.toString:()Ljava/lang/String;
 * #9 = Methodref          #30.#31        // java/io/PrintStream.println:(Ljava/lang/String;)V
 * #10 = Class              #32            // com/bruma/HelloByteCode
 * #11 = Class              #33            // java/lang/Object
 * #12 = Utf8               <init>
 * #13 = Utf8               ()V
 * #14 = Utf8               Code
 * #15 = Utf8               LineNumberTable
 * #16 = Utf8               main
 * #17 = Utf8               ([Ljava/lang/String;)V
 * #18 = Utf8               StackMapTable
 * #19 = Class              #34            // "[Ljava/lang/String;"
 * #20 = Utf8               SourceFile
 * #21 = Utf8               HelloByteCode.java
 * #22 = NameAndType        #12:#13        // "<init>":()V
 * #23 = Class              #35            // java/lang/System
 * #24 = NameAndType        #36:#37        // out:Ljava/io/PrintStream;
 * #25 = Utf8               java/lang/StringBuilder
 * #26 = NameAndType        #38:#39        // append:(I)Ljava/lang/StringBuilder;
 * #27 = Utf8               为偶数
 * #28 = NameAndType        #38:#40        // append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
 * #29 = NameAndType        #41:#42        // toString:()Ljava/lang/String;
 * #30 = Class              #43            // java/io/PrintStream
 * #31 = NameAndType        #44:#45        // println:(Ljava/lang/String;)V
 * #32 = Utf8               com/bruma/HelloByteCode
 * #33 = Utf8               java/lang/Object
 * #34 = Utf8               [Ljava/lang/String;
 * #35 = Utf8               java/lang/System
 * #36 = Utf8               out
 * #37 = Utf8               Ljava/io/PrintStream;
 * #38 = Utf8               append
 * #39 = Utf8               (I)Ljava/lang/StringBuilder;
 * #40 = Utf8               (Ljava/lang/String;)Ljava/lang/StringBuilder;
 * #41 = Utf8               toString
 * #42 = Utf8               ()Ljava/lang/String;
 * #43 = Utf8               java/io/PrintStream
 * #44 = Utf8               println
 * #45 = Utf8               (Ljava/lang/String;)V
 * {
 * public com.bruma.HelloByteCode();
 * descriptor: ()V
 * flags: ACC_PUBLIC
 * Code:
 * stack=1, locals=1, args_size=1
 * 0: aload_0
 * 1: invokespecial #1                  // Method java/lang/Object."<init>":()V
 * 4: return
 * LineNumberTable:
 * line 10: 0
 * <p>
 * public static void main(java.lang.String[]);
 * descriptor: ([Ljava/lang/String;)V
 * flags: ACC_PUBLIC, ACC_STATIC
 * Code:
 * stack=3, locals=6, args_size=1
 * 0: iconst_1
 * 1: istore_1
 * 2: iconst_2
 * 3: istore_2
 * 4: iconst_3
 * 5: istore_3
 * 6: iconst_4
 * 7: istore        4
 * 9: iconst_0
 * 10: istore        5
 * 12: iload         5
 * 14: bipush        10
 * 16: if_icmpge     58
 * 19: iload         5
 * 21: iload_2
 * 22: idiv
 * 23: ifne          52
 * 26: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
 * 29: new           #3                  // class java/lang/StringBuilder
 * 32: dup
 * 33: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
 * 36: iload         5
 * 38: invokevirtual #5                  // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
 * 41: ldc           #6                  // String 为偶数
 * 43: invokevirtual #7                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
 * 46: invokevirtual #8                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
 * 49: invokevirtual #9                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
 * 52: iinc          5, 1
 * 55: goto          12
 * 58: return
 * LineNumberTable:
 * line 12: 0
 * line 13: 2
 * line 14: 4
 * line 15: 6
 * line 17: 9
 * line 18: 19
 * line 19: 26
 * line 17: 52
 * line 22: 58
 * StackMapTable: number_of_entries = 3
 * frame_type = 255 /* full_frame
 * <p>
 * import com.sun.org.glassfish.gmbal.Description;* @ClassName HelloByteCode
 *
 * @Author: Created By bruceMa
 * @Description Java 字节码
 * @Date: 2020/10/15 10:48 下午
 * @Version 1.0
 */
public class HelloByteCode {
    public static void main(String[] args) {
        int a = 1;
        int b = 2;
        int c = 3;
        int d = 4;

        for (int i = 0; i < 10; i++) {
            if (i / b == 0) {
                System.out.println(i + "为偶数");
            }
        }
    }
}
