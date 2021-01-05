package com.boring.dal.dao.impl.hibernate;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.TxFlusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@EnableTransactionManagement
@Configuration
public class HibernateConf {

    private final static Logger logger = LogManager.getLogger(HibernateConf.class);


    @Resource
    private DataAccessConfig dataAccessConfig;

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.generate_statistics", "false");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "false");
        hibernateProperties.setProperty("hibernate.transaction.coordinator_class", "jdbc");
        hibernateProperties.setProperty("hibernate.statement_cache.size", "50");
        hibernateProperties.setProperty("hibernate.jdbc.fetch_size", "100");
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "100");
        hibernateProperties.setProperty("hibernate.jdbc.use_scrollable_resultset", "true");
        hibernateProperties.setProperty("hibernate.jdbc.use_streams_for_binary", "true");
        hibernateProperties.setProperty("hibernate.max_fetch_depth", "3");
        hibernateProperties.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
        hibernateProperties.setProperty("hibernate.query.substitutions", "true 1, false 0");
        hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");
        hibernateProperties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        hibernateProperties.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLInnoDBDialect");
        hibernateProperties.setProperty("hibernate.jdbc.use_get_generated_keys", "true");

        return hibernateProperties;
    }
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource ds) throws Exception {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(ds);
        List<Class> clst = dataAccessConfig.loadAllClazz();
        StringBuilder log = new StringBuilder("\n");
        for (Iterator<Class> iterator = clst.iterator(); iterator.hasNext(); ) {
            Class c = iterator.next();
            log.append(c.getTypeName()).append("\n");
        }
        logger.info("load " + clst.size() + " class:" + log.toString());
        sessionFactory.setAnnotatedClasses(clst.toArray(new Class[0]));
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Autowired(required = false)
    private TxFlusher txCleaner;

    @Bean
    public PlatformTransactionManager hibernateTransactionManager(LocalSessionFactoryBean sessionFactoryBean) throws Exception {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager() {
            @Override
            protected void doCleanupAfterCompletion(Object transaction) {
                super.doCleanupAfterCompletion(transaction);
                if (txCleaner != null)
                    txCleaner.txClean(transaction);
            }
        };
        transactionManager.setSessionFactory(sessionFactoryBean.getObject());

        return transactionManager;
    }



}
