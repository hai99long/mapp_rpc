package com.wing.mapp.common.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by wanghl on 2017/6/1.
 * reference标签注册
 */
public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("reference", new RpcReferenceParser());
    }
}
