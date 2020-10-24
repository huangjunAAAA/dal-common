package com.boring.dal.remote.autorpc;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RpcClientRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private String clientPkg="com.rpc.client";

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String clientPkg=readProperty((ConfigurableEnvironment) applicationContext.getEnvironment(),"client.package");
        Reflections reflections = new Reflections(clientPkg, new TypeAnnotationsScanner(), new SubTypesScanner());

        Set<Class<?>> beanClazzs = reflections.getTypesAnnotatedWith(AutoRpc.class);
        for (Class beanClazz : beanClazzs) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.setFactoryBeanName("rpcClientFactory");
            definition.setFactoryMethodName("buildClientProxy");
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
            definition.setBeanClass(beanClazz);
            registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }


    private String readProperty(ConfigurableEnvironment environment, String pkey) {
        for (PropertySource<?> source : environment.getPropertySources()) {
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> propertySource = (EnumerablePropertySource<?>) source;
                for (String property : propertySource.getPropertyNames()) {
                    if (pkey.equals(property))
                    {
                        return (String)propertySource.getProperty(pkey);
                    }
                }
            }
        }
        throw new IllegalStateException("Unable to determine value of property " + pkey);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
