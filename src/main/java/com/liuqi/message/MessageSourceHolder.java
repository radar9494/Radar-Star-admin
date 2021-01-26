package com.liuqi.message;

import com.liuqi.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class MessageSourceHolder {
    /**
     *
     *@param code：对应messages配置的key.
     *@return
     */
    public static String getMessage(String code){
        MessageSource messageSource=(MessageSource) SpringUtil.getBean("messageSource");
        //这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code,null,"",locale);
    }
    /**
     *指定语言.
     *@param code
     *@param args
     *@param defaultMessage
     *@param locale
     *@return
     */
    public static String getMessage(String code,Object[]args,String defaultMessage,Locale locale){
        MessageSource messageSource=(MessageSource)SpringUtil.getBean("messageSource");
        return messageSource.getMessage(code,args,defaultMessage,locale);
    }
}
