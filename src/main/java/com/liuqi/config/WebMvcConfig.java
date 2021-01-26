package com.liuqi.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.liuqi.anno.admin.CurAdminIDMethodArgumentResolver;
import com.liuqi.anno.sys.CurSysIDMethodArgumentResolver;
import com.liuqi.anno.user.CurUserIDMethodArgumentResolver;
import com.liuqi.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * WebMvcConfig 配置文件 添加拦截器
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("index");
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);

        FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter4
                = new FastJsonHttpMessageConverter4();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 空值特别处理
        // WriteNullListAsEmpty 将Collection类型字段的字段空值输出为[]
        // WriteNullStringAsEmpty 将字符串类型字段的空值输出为空字符串 ""
        // WriteNullNumberAsZero 将数值类型字段的空值输出为0
        // WriteNullBooleanAsFalse 将Boolean类型字段的空值输出为false
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteNullStringAsEmpty);

        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastJsonHttpMessageConverter4.setSupportedMediaTypes(fastMediaTypes);

        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter4.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter4);
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public UserLoginInterceptor userLoginInterceptor() {
        return new UserLoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //前台用户登录
        registry.addInterceptor(userLoginInterceptor())
                .excludePathPatterns("/front/login","/front/checkPwd","/front/register","/front/forgetPassword",
                        "/search/**","/websocket/**","/front/registerCheck")
                .addPathPatterns("/front/**");
        super.addInterceptors(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new CurUserIDMethodArgumentResolver());
        argumentResolvers.add(new CurSysIDMethodArgumentResolver());
        argumentResolvers.add(new CurAdminIDMethodArgumentResolver());
    }

}
