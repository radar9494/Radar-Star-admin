package com.liuqi.config;

import com.google.common.collect.Maps;
import com.liuqi.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class XssConfig {
    /**
     * xss过滤拦截器
     */
    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/front/*");
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludes", "/favicon.ico,/images/*,/js/*,/css/*,/skin/*");
        initParameters.put("isIncludeRichText", "false");//是否过滤富文本内容
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

}
