package com.liuqi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="activemq")
public class ActiveMqInfo {
    private String brokerUrl;
    private Boolean inMemory;
    private String password;
    private String user;
    private Boolean useExponentialBackOff;
    private Integer  maximumRedeliveries;
    private Integer initialRedeliveryDelay;
    private Integer backOffMultiplier;
    private Boolean useCollisionAvoidance;
    private Integer maximumRedeliveryDelay;
    private Integer poolMaxConnections;
    private Integer poolIdleTimeout;
    private Integer poolExpiryTimeout;
}
