package com.yangyang.thrift.spring.client;

import com.yangyang.thrift.spring.common.ThriftSpringConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * thrift client class 扫描
 */
public class ThriftClientClassPathScanner extends ClassPathBeanDefinitionScanner {
	
	private Logger thriftClientLogger = LoggerFactory.getLogger(ThriftSpringConst.thriftLog) ;
	
	private Map<String, Object> thriftConfig ;

	public ThriftClientClassPathScanner(BeanDefinitionRegistry registry, Map<String, Object> thriftConfig) {
		super(registry, false);
		this.thriftConfig = thriftConfig ;
	}

	public void registerFilters() {
		
		addIncludeFilter(new TypeFilter() {

			public boolean match(MetadataReader metadataReader,
					MetadataReaderFactory metadataReaderFactory) throws IOException {
				return true;
			}
		});

		addExcludeFilter(new TypeFilter() {

			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return className.endsWith("package-info");
			}
		});
	}

	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		
		thriftClientLogger.info("ThriftClientClassPathScanner doScan. thriftConfig:" + thriftConfig + ",basePackage" + Arrays.toString(basePackages));
		
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (!beanDefinitions.isEmpty()) {
			for (BeanDefinitionHolder holder : beanDefinitions) {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
				
				thriftClientLogger.info("ThriftClientClassPathScanner doScan. add bean beanclassname:" + definition.getBeanClassName());
				definition.getPropertyValues().addPropertyValues(thriftConfig) ;

				definition.getPropertyValues().add("thriftInterface", definition.getBeanClassName());
				definition.getPropertyValues().add("thriftImpl", definition.getBeanClassName().replace("Iface", "Client"));
				// serverName
				String className = definition.getBeanClassName().replace("$Iface", "") ;
				className = className.substring(className.lastIndexOf(".") + 1,className.length()) ;
//				char c[] = new char[1] ;
//				c[0] = className.charAt(0) ;
//				String tmp = new String(c) ;
//				className = className.replaceFirst(tmp, tmp.toLowerCase()) ;
				definition.getPropertyValues().add("serverName", className);
				
				definition.setBeanClass(ThriftClientFactoryBean.class);
			}
		} else {
			thriftClientLogger.info("ThriftClientClassPathScanner doScan. package no beanDefinitions");
		}

		return beanDefinitions;
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
	}

	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition)
			throws IllegalStateException {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		}
		else {
			thriftClientLogger.warn("Skipping ThriftClientFactoryBean with name '" + beanName
					+ "' and '"
					+ beanDefinition.getBeanClassName()
					+ "' thriftInterface"
					+ ". Bean already defined with the same name!");
			return false;
		}
	}
}
