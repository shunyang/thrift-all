package com.yangyang.thrift.spring.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * thrift client 扫描配置

 */
public class ThriftClientScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

	private String basePackage;
	
	private Map<String, Object> thriftConfig ;
	
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
	public void setThriftConfig(Map<String, Object> thriftConfig) {
		this.thriftConfig = thriftConfig;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		ThriftClientClassPathScanner scanner = new ThriftClientClassPathScanner(registry, thriftConfig);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}
}
