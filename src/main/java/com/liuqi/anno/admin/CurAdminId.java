package com.liuqi.anno.admin;

import java.lang.annotation.*;

/**
 * 自动以注解 解析session中的userId 注入到controller的方法中
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurAdminId {
}
