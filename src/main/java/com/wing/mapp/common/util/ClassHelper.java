package com.wing.mapp.common.util;

/**
 * Created by wanghl on 2017/3/26.
 */
public class ClassHelper {
    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        return caller.getClassLoader();
    }
}
