package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();  // 线程变量,一次请求为一个线程,ThreadLocal为每个线程提供一个变量,
                                                                        // 一个线程中存储一个值,只能在同一个线程中获取和设置值

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
