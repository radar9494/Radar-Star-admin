package com.liuqi.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * i18n设置
 */
@Configuration
public class MessageConfig {
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr =new SessionLocaleResolver();
        //设置默认区域,
        slr.setDefaultLocale(Locale.CHINA);
        return slr;
    }


    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource){
        LocalValidatorFactoryBean factoryBean=new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }
}
