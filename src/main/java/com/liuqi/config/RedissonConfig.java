package com.liuqi.config;

import com.liuqi.redis.lock.DistributedLocker;
import com.liuqi.redis.lock.RedissonDistributedLocker;
import com.liuqi.redis.lock.RedissonLockUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String address;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    private int timeout = 3000;

    private int database = 0;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize = 10;


    /**
     private int slaveConnectionPoolSize = 250;

     private int masterConnectionPoolSize = 250;

     private String[] sentinelAddresses;

     private String masterName;


     * 哨兵模式自动装配
     * @return

     @Bean
     @ConditionalOnProperty(name="redisson.master-name") RedissonClient redissonSentinel() {
     Config config = new Config();
     SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(sentinelAddresses)
     .setMasterName(masterName)
     .setTimeout(timeout)
     .setMasterConnectionPoolSize(masterConnectionPoolSize)
     .setSlaveConnectionPoolSize(slaveConnectionPoolSize);

     if(StringUtils.isNotBlank(password)) {
     serverConfig.setPassword(password);
     }
     return Redisson.create(config);
     }*/

    /**
     * 单机模式自动装配
     *
     * @return
     */
    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + address + ":" + port)
                .setTimeout(timeout)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize);

        if (StringUtils.isNotBlank(password)) {
            serverConfig.setPassword(password);
        }

        return Redisson.create(config);
    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     *
     * @return
     */
    @Bean
    public DistributedLocker distributedLocker(RedissonClient redissonClient) {
        RedissonDistributedLocker locker = new RedissonDistributedLocker();
        locker.setRedissonClient(redissonClient);
        RedissonLockUtil.setLocker(locker);
        return locker;
    }
}
