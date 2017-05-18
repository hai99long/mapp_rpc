package com.wing.mapp.common;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wanghl on 2017/3/31.
 */
public final class URL implements Serializable {
    private static final long serialVersionUID = -1985165475234910535L;

    private final String protocol;

    private final String username;

    private final String password;

    private final String host;

    private final int port;

    private final String path;

    private final Map<String, String> parameters;
    protected URL() {
        this.protocol = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.path = null;
        this.parameters = null;
    }
}
