package com.xiaogenban1993;

import java.lang.instrument.Instrumentation;

/**
 * @author Frank
 * @date 2024/7/26 19:18
 */
public class MyAgent {
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.printf("thread: %s, args: %s, classLoader: %s%n", Thread.currentThread().getName(), args, MyAgent.class.getClassLoader());
    }
}
