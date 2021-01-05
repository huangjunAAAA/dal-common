package com.boring.dal.dao.impl.jta;

import com.boring.dal.dao.TxFlusher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
public class JTAConf {

    @Autowired(required = false)
    private TxFlusher txCleaner;


    @Bean
    public PlatformTransactionManager transactionManager(ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) throws Exception {
        JpaTransactionManager transactionManager = new JpaTransactionManager(){
            @Override
            protected void doCleanupAfterCompletion(Object transaction) {
                super.doCleanupAfterCompletion(transaction);
                if (txCleaner != null)
                    txCleaner.txClean(transaction);
            }
        };
        TransactionManagerCustomizers tc = transactionManagerCustomizers.getIfAvailable();
        if ( tc != null) {
            tc.customize(transactionManager);
        }
        return transactionManager;
    }
}
