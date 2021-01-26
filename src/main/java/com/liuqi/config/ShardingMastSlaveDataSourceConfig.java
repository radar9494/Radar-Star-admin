package com.liuqi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import io.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

//@Slf4j
//@Configuration
//@EnableConfigurationProperties(ShardingMastSlaveConfig.class)
public class ShardingMastSlaveDataSourceConfig {


        @Autowired
        private ShardingMastSlaveConfig shardingMastSlaveConfig;

        @Bean("dataSource")
        public DataSource dataSource() throws SQLException {
            shardingMastSlaveConfig.getDataSources().forEach((k, v) -> configDataSource(v));
            Map<String, DataSource> dataSourceMap = Maps.newHashMap();
            dataSourceMap.putAll(shardingMastSlaveConfig.getDataSources());
            DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, shardingMastSlaveConfig.getMasterSlave(), Maps.newHashMap(), new Properties());
            //log.info("masterSlaveDataSource config complete");
            return dataSource;
        }

        private void configDataSource(DruidDataSource druidDataSource) {
            druidDataSource.setMaxActive(50);
            druidDataSource.setInitialSize(1);
            druidDataSource.setMaxWait(60000);
            druidDataSource.setMinIdle(1);
            druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
            druidDataSource.setMinEvictableIdleTimeMillis(300000);
            druidDataSource.setValidationQuery("select 'x'");
            druidDataSource.setTestWhileIdle(true);
            druidDataSource.setTestOnBorrow(false);
            druidDataSource.setTestOnReturn(false);
            druidDataSource.setPoolPreparedStatements(true);
            druidDataSource.setMaxOpenPreparedStatements(20);
            druidDataSource.setUseGlobalDataSourceStat(true);
            try {
                druidDataSource.setFilters("stat,wall,slf4j");
            } catch (SQLException e) {
                //log.error("druid configuration initialization filter", e);
            }
        }

}
