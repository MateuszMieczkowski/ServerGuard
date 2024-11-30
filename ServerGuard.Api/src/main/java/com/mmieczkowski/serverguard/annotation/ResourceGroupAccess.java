package com.mmieczkowski.serverguard.annotation;

import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceGroupAccess {
    String value() default "resourceGroupId";
    ResourceGroupUserRole[] roles() default {ResourceGroupUserRole.ADMIN};
}