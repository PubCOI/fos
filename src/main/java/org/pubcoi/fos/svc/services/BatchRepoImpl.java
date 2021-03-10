package org.pubcoi.fos.svc.services;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile("batch")
@Service
public class BatchRepoImpl implements BatchRepo {

    JobRepository jobRepository;
    JobExecutionDao jobExecutionDao;
    JobExplorer jobExplorer;
    final DataSource dataSource;
    final PlatformTransactionManager transactionManager;
    final JdbcTemplate template;

    @Value("${batch.table.prefix}")
    String tablePrefix;

    // most of this is nicked from elsewhere;
    // thanks to https://www.programcreek.com/java-api-examples/?code=
    // tuxdevelop%2Fspring-batch-lightmin%2Fspring-batch-lightmin-master
    // %2Fspring-batch-lightmin-core%2Fspring-batch-lightmin-core-batch%2F
    // src%2Fmain%2Fjava%2Forg%2Ftuxdevelop%2Fspring%2Fbatch%2Flightmin%2F
    // batch%2Fconfiguration%2FDefaultSpringBatchLightminBatchConfigurer.java

    private final DataFieldMaxValueIncrementer incrementer = new AbstractDataFieldMaxValueIncrementer() {
        @Override
        protected long getNextKey() {
            throw new IllegalStateException("JobExplorer is read only.");
        }
    };

    public BatchRepoImpl(DataSource dataSource, PlatformTransactionManager transactionManager, JdbcTemplate template) {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.template = template;
    }

    @PostConstruct
    public void setup() {
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setTablePrefix(tablePrefix);
        factoryBean.setDataSource(this.dataSource);
        try {
            factoryBean.afterPropertiesSet();
            this.jobExplorer = factoryBean.getObject();
            this.jobRepository = this.createJobRepository();
            this.jobExecutionDao = this.createJobExecutionDao();
        } catch (Exception e) {
            throw new RuntimeException("Unable to set up Batch endpoint, check configuration");
        }
    }

    @Bean
    JobExplorer getJobExplorer() {
        return this.jobExplorer;
    }

    protected JobRepository createJobRepository() throws Exception {
        final JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(this.dataSource);
        jobRepositoryFactoryBean.setTransactionManager(this.transactionManager);
        jobRepositoryFactoryBean.setTablePrefix(this.tablePrefix);
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean.getObject();
    }

    protected JobExecutionDao createJobExecutionDao() throws Exception {
        final JdbcJobExecutionDao dao = new JdbcJobExecutionDao();
        dao.setJdbcTemplate(this.template);
        dao.setJobExecutionIncrementer(this.incrementer);
        dao.setTablePrefix(this.tablePrefix);
        dao.afterPropertiesSet();
        return dao;
    }

}
