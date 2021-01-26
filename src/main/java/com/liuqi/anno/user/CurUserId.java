package com.liuqi.anno.user;

import java.lang.annotation.*;

/**
 * 自动以注解 token 注入到controller的方法中
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurUserId {
    String value() default "";
}
