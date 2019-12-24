package com.zspace.spring.configure.property;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 对于属性类的bean注册后置处理器
 * @author liuwenqing02
 *
 */
public class ConfigurationPropertiesBindingPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		if (!registry.containsBeanDefinition(ConfigurationPropertiesBindingPostProcessor.BEAN_NAME)) {
			registerConfigurationPropertiesBindingPostProcessor(registry);
		}
	}

	private void registerConfigurationPropertiesBindingPostProcessor(BeanDefinitionRegistry registry) {
		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(ConfigurationPropertiesBindingPostProcessor.class);
		definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		registry.registerBeanDefinition(ConfigurationPropertiesBindingPostProcessor.BEAN_NAME, definition);
		if(registry instanceof AbstractBeanFactory) {
		    AbstractBeanFactory bf = (AbstractBeanFactory) registry;
		    bf.addBeanPostProcessor(bf.getBean(ConfigurationPropertiesBindingPostProcessor.class));
		}
		
	}

}
