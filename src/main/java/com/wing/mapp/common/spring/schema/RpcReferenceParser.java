package com.wing.mapp.common.spring.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by wanghl on 2017/6/3.
 * reference标签的解析
 */
public class RpcReferenceParser implements BeanDefinitionParser {
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String interfaceName = element.getAttribute("interfaceName");
        String address = element.getAttribute("address");
        String protocol = element.getAttribute("protocol");
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(RpcReference.class);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName",interfaceName);
        beanDefinition.getPropertyValues().addPropertyValue("address",address);
        beanDefinition.getPropertyValues().addPropertyValue("protocol",protocol);
        parserContext.getRegistry().registerBeanDefinition(id,beanDefinition);
        return beanDefinition;
    }
}
