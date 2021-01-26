package com.liuqi.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix="sharding")
public class ShardingMastSlaveConfig {
    private Map<String, DruidDataSource> dataSources = new HashMap<>();
    private MasterSlaveRuleConfiguration masterSlave;
}
