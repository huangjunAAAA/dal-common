package com.boring.dal;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.sqllinker.FieldSQLConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"application.yml", "bootstrap.yml"}, factory = YamlPropertySourceFactory.class)
public class ConfigConf {
    @Value("${dao.config}")
    private String configFile;

    @Bean(name = "dataAccessConfig")
    public DataAccessConfig loadConfig(FieldSQLConnector linker) {
        return new DataAccessConfig(configFile,linker);
    }
}
