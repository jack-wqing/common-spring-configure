package com.zspace.spring.configure.property;

import java.util.Properties;

/**
 * 获取系统使用的所有配置属性
 * @author liuwenqing02
 *
 */
public interface FindPropertySource {

    default public Properties propretysSources() {
        return null;
    }
    
    default public Properties propretysSources(ClassLoader beanClassLoader) {
        return null;
    }
    
}
