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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RpcClientRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private String clientPkg;

    private ApplicationContext applicationContext;
    private List<String> clientSkip;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        clientPkg=readProperty((ConfigurableEnvironment) applicationContext.getEnvironment(),"client.package");

        clientSkip = readCollectionProperty((ConfigurableEnvironment) applicationContext.getEnvironment(), "client.skip");
        Map<String, String> skipClazz = clientSkip.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        Reflections reflections = new Reflections(clientPkg, new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> beanClazzs = reflections.getTypesAnnotatedWith(AutoRpc.class);
        for (Class beanClazz : beanClazzs) {
            if(skipClazz.containsKey(beanClazz.getTypeName()))
                continue;
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

    private List<String> readCollectionProperty(ConfigurableEnvironment environment, String pkey) {
        ArrayList<String> ret=new ArrayList<>();
        for (PropertySource<?> source : environment.getPropertySources()) {
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> propertySource = (EnumerablePropertySource<?>) source;
                for (String property : propertySource.getPropertyNames()) {
                    if (property.matches(pkey+"\\[\\d+\\]"))
                    {
                        Object v = propertySource.getProperty(property);
                        ret.add(v.toString());
                    }
                }
            }
        }
        return ret;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
