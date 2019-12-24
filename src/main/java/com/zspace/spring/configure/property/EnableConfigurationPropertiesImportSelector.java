package com.zspace.spring.configure.property;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;


/**
 * 对属性对应的文件：进行属性注入
 */
class EnableConfigurationPropertiesImportSelector implements ImportSelector {

	private static final String[] IMPORTS = { ConfigurationPropertiesBeanRegistrar.class.getName(),
			ConfigurationPropertiesBindingPostProcessorRegistrar.class.getName() };

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		return IMPORTS;
	}
	
	/**
	 * 注册配置属性类
	 */
	public static class ConfigurationPropertiesBeanRegistrar implements ImportBeanDefinitionRegistrar {

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			getTypes(metadata).forEach((type) -> register(registry, (ConfigurableListableBeanFactory) registry, type));
		}

		private List<Class<?>> getTypes(AnnotationMetadata metadata) {
			MultiValueMap<String, Object> attributes = metadata
					.getAllAnnotationAttributes(EnableConfigurationProperties.class.getName(), false);
			return collectClasses((attributes != null) ? attributes.get("value") : Collections.emptyList());
		}

		private List<Class<?>> collectClasses(List<?> values) {
			return values.stream().flatMap((value) -> Arrays.stream((Object[]) value)).map((o) -> (Class<?>) o)
					.filter((type) -> void.class != type).collect(Collectors.toList());
		}

		private void register(BeanDefinitionRegistry registry, ConfigurableListableBeanFactory beanFactory,
				Class<?> type) {
			String name = getName(type);
			if (!containsBeanDefinition(beanFactory, name)) {
				registerBeanDefinition(registry, name, type);
			}
		}

		private String getName(Class<?> type) {
		    ConfigProperties annotation = AnnotationUtils.findAnnotation(type, ConfigProperties.class);
			String prefix = (annotation != null) ? annotation.prefix() : "";
			return (StringUtils.hasText(prefix) ? prefix + "-" + type.getName() : type.getName());
		}

		private boolean containsBeanDefinition(ConfigurableListableBeanFactory beanFactory, String name) {
			if (beanFactory.containsBeanDefinition(name)) {
				return true;
			}
			BeanFactory parent = beanFactory.getParentBeanFactory();
			if (parent instanceof ConfigurableListableBeanFactory) {
				return containsBeanDefinition((ConfigurableListableBeanFactory) parent, name);
			}
			return false;
		}

		private void registerBeanDefinition(BeanDefinitionRegistry registry, String name, Class<?> type) {
			assertHasAnnotation(type);
			GenericBeanDefinition definition = new GenericBeanDefinition();
			definition.setBeanClass(type);
			registry.registerBeanDefinition(name, definition);
		}

		private void assertHasAnnotation(Class<?> type) {
			Assert.notNull(AnnotationUtils.findAnnotation(type, ConfigProperties.class),
					() -> "No " + ConfigProperties.class.getSimpleName() + " annotation found on  '"
							+ type.getName() + "'.");
		}

	}

}
