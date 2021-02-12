package com.boring.dal.dao;

import org.apache.shardingsphere.core.yaml.config.masterslave.YamlRootMasterSlaveConfiguration;
import org.apache.shardingsphere.core.yaml.swapper.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.underlying.common.yaml.engine.YamlEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

//@Configuration
public class DataSourceConf {

    @Value("${datasource.config}")
    private String dsconfig;

    @Bean
    public DataSource dataSource(YamlRootMasterSlaveConfiguration config) throws Exception {
        return MasterSlaveDataSourceFactory.createDataSource(config.getDataSources(), new MasterSlaveRuleConfigurationYamlSwapper().swap(config.getMasterSlaveRule()), config.getProps());
    }

    @Bean
    public YamlRootMasterSlaveConfiguration shardingConfig() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File yamlFile = new File(classLoader.getResource(dsconfig).getFile());
        YamlRootMasterSlaveConfiguration config = YamlEngine.unmarshal(yamlFile, YamlRootMasterSlaveConfiguration.class);
        return config;
    }
}
