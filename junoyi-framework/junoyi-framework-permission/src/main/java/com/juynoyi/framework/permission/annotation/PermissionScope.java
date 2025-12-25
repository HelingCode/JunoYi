package com.juynoyi.framework.permission.annotation;

import com.juynoyi.framework.permission.enums.PermissionType;

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

    /**
     * 权限码（支持通配符）
     * @return
     */
    String[] value() default {};

    /**
     * 权限类型（默认API类型）
     * @return
     */
    PermissionType type() default PermissionType.API;


}