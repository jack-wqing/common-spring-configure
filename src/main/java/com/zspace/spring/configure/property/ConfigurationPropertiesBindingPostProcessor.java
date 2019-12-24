package com.zspace.spring.configure.property;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;

import com.zspace.spring.configure.reflect.DefaultReflectorFactory;
import com.zspace.spring.configure.reflect.Reflector;
import com.zspace.spring.configure.reflect.ReflectorFactory;

/**
 * 对属性类型的bean进行后缀处理
 * @author liuwenqing02
 *
 */
public class ConfigurationPropertiesBindingPostProcessor
		implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	public static final String BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class.getName();

	private ApplicationContext applicationContext;

	private ReflectorFactory reflectorFactory;
	
	private PropertySources propertySources;

	private ConversionService conversionService;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	    this.reflectorFactory = new DefaultReflectorFactory(); 
	    this.propertySources = new PropertySourcesDeducer(this.applicationContext).getPropertySources();
	    this.conversionService = new DefaultConversionService();
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	    ConfigProperties annotation = getAnnotation(bean, beanName, ConfigProperties.class);
		if (annotation != null) {
			bind(bean, beanName, annotation);
		}
		return bean;
	}

	private void bind(Object bean, String beanName, ConfigProperties annotation) {
	    Reflector refector = this.reflectorFactory.findForClass(bean.getClass());
	    String[] proNames = refector.getSetablePropertyNames();
	    String prefix = annotation.prefix();
	    prefix = prefix.endsWith(".") ? prefix : prefix + ".";
	    if(proNames != null) {
	        for (String proname : proNames) {
                String key = prefix + proname;
                Class<?> proType = refector.getSetterType(proname);
                propertySources.forEach((PropertySource<?> source) -> {
                    Object value = source.getProperty(key);
                    if (value != null) {
                        try {
                            if(conversionService.canConvert(value.getClass(), proType)) {
                                value = conversionService.convert(value, proType);
                            }
                            refector.getSetInvoker(proname).invoke(bean, new Object[]{value});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
	    }
	}
	private <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
		A annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
		return annotation;
	}

}
