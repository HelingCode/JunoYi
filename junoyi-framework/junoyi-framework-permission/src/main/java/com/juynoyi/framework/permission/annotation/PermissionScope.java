package com.juynoyi.framework.permission.annotation;

import java.lang.annotation.*;

/**
 * 接口方法权限标记注解
 *
 * @author Fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PermissionScope {
    String[] value() default {};
}