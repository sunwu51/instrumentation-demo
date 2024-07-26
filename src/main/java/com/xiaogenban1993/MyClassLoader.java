package com.xiaogenban1993;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Frank
 * @date 2024/7/26 19:29
 */
public class MyClassLoader extends URLClassLoader {
    public final String name;

    public MyClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
