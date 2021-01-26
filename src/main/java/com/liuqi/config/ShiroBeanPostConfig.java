package com.liuqi.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroBeanPostConfig {
    @Bean
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() { return new LifecycleBeanPostProcessor(); }
}
