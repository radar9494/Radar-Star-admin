package com.liuqi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.liuqi.jobtask.QuartzJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 定时任务
 */
@Configuration
public class SchedulerConfig {

    @Value("${default.dataSource}")
    private String dataSourceName;

    @Autowired
    private ShardingMastSlaveConfig shardingMastSlaveConfig;


    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(QuartzJobFactory myJobFactory) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.setDataSource(shardingMastSlaveConfig.getDataSources().get(dataSourceName));
        //使job实例支持spring 容器管理
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setJobFactory(myJobFactory);
        // 延迟10s启动quartz
        schedulerFactoryBean.setStartupDelay(10);

        return schedulerFactoryBean;
    }
//    @Bean
//    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws IOException, SchedulerException {
//        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//        scheduler.start();
//        return scheduler;
//    }

    @Bean
    public Properties quartzProperties() throws IOException {
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceName", "coinScheduler");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        prop.put("org.quartz.scheduler.jmx.export", "true");

        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        prop.put("org.quartz.jobStore.isClustered", "true");

        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        prop.put("org.quartz.jobStore.dataSource", "myDS");
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        prop.put("org.quartz.jobStore.misfireThreshold", "120000");
        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
        prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE");

        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        //数据库设置
        /*prop.put("org.quartz.dataSource.myDS.driver", driverClassName);
        prop.put("org.quartz.dataSource.myDS.URL", url);
        prop.put("org.quartz.dataSource.myDS.user", username);
        prop.put("org.quartz.dataSource.myDS.password", password);
        prop.put("org.quartz.dataSource.myDS.maxConnections", 10);*/
        return prop;
    }

}
