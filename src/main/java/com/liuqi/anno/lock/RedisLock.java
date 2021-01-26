package com.liuqi.anno.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RedisLock {
    String lockPrefix() default "";
    //超时时间 默认秒
    int timeOut() default 10;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
