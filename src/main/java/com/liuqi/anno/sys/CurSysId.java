package com.liuqi.anno.sys;

import java.lang.annotation.*;

/**
 * 自动以注解 解析session中的sysId 注入到controller的方法中
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurSysId {
    String value() default "";
}
