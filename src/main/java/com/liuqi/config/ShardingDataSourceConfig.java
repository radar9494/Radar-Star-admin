package com.liuqi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuqi.sharingjdbc.TableShardingIntegerAlgorithm;
import com.liuqi.sharingjdbc.TableShardingLongAlgorithm;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 分片
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ShardingMastSlaveConfig.class)
public class ShardingDataSourceConfig {
    @Value("${default.dataSource}")
    private String dataSourceName;
    @Autowired
    private ShardingMastSlaveConfig shardingMastSlaveConfig;

    @Bean("dataSource")
    public DataSource dataSource() throws SQLException {
        shardingMastSlaveConfig.getDataSources().forEach((k, v) -> configDataSource(v));
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        dataSourceMap.putAll(shardingMastSlaveConfig.getDataSources());
        //DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, shardingMastSlaveConfig.getMasterSlave(), Maps.newHashMap());

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getTrusteeTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getTradeRecordTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getTradeRecordUserTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getWalletLogTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getKDataTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getLockWalletLogTableRuleConfiguration());
        //shardingRuleConfig.getBindingTableGroups().add("t_user_wallet_log");
        //全局分库配置
        //shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", DatabaseShardingAlgorithm.class.getName()));
        //全局分表配置
        //shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", ModuloShardingTableAlgorithm.class.getName()));
        shardingRuleConfig.setMasterSlaveRuleConfigs(getMasterSlaveRuleConfigurations());
        shardingRuleConfig.setDefaultDataSourceName(dataSourceName);
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new HashMap<String, Object>(), new Properties());
    }

    private TableRuleConfiguration getTrusteeTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_trustee");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_trustee_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分库
        //orderTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", DatabaseShardingAlgorithm.class.getName()));
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private TableRuleConfiguration getTradeRecordTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_trade_record");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_trade_record_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("trade_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private TableRuleConfiguration getTradeRecordUserTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_trade_record_user");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_trade_record_user_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private TableRuleConfiguration getWalletLogTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_user_wallet_log");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_user_wallet_log_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private TableRuleConfiguration getKDataTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_k_data");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_k_data_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("trade_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private TableRuleConfiguration getLockWalletLogTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_lock_wallet_log");
        orderTableRuleConfig.setActualDataNodes("ds_0.t_lock_wallet_log_${[0,1,2,3,4,5,6,7,8,9]}");
        //单个表的分表
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", new TableShardingLongAlgorithm()));
        orderTableRuleConfig.setKeyGeneratorColumnName("id");
        return orderTableRuleConfig;
    }

    private List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurations() {
        MasterSlaveRuleConfiguration masterSlaveRuleConfig1 = shardingMastSlaveConfig.getMasterSlave();
        return Lists.newArrayList(masterSlaveRuleConfig1);
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
