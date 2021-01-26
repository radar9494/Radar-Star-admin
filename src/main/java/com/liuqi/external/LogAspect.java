package com.liuqi.external;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import org.apache.log4j.NDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Order(1)
@Component
public class LogAspect {
    private static Log log= Log4j2LogFactory.get("api");
    @Pointcut("execution(public * com.liuqi.external.api.*.* (..))")
    public  void apiLogAspect(){}

    @Before("apiLogAspect()")
    public void doBefore(JoinPoint joinPoint){
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes)ra;
        HttpServletRequest request=sra.getRequest();
        MDC.put("uri",request.getRequestURI());
        //记录请求内容
        log.info("地址:"+request.getRemoteAddr());
        log.info("请求:"+joinPoint.getSignature().getDeclaringType()+"方法："+joinPoint.getSignature().getName());
        MDC.get("uri");
        MDC.remove("uri");
    }

    @AfterReturning(returning = "ret",pointcut = "apiLogAspect()")
    public void doAfterReturning(Object ret) throws Throwable{
        log.info("返回"+ret);
    }
}
