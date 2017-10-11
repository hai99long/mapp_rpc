package com.wing.mapp.common.util;

/**
 * Created by wanghl on 2017/3/26.
 */
public class ClassHelper {
    /**
     * 获得调用者的classloader对象
     * @param caller
     * @return
     */
    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        return caller.getClassLoader();
    }
}
