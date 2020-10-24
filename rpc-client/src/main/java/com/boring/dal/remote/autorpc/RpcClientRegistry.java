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
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RpcClientRegistry implements BeanDefinitionRegistryPostProcessor {

    private String clientPkg="com.rpc.client";

    Set<Class<?>> beanClazzs;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Reflections reflections = new Reflections(clientPkg, new TypeAnnotationsScanner(), new SubTypesScanner());

        beanClazzs = reflections.getTypesAnnotatedWith(AutoRpc.class);
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
}
