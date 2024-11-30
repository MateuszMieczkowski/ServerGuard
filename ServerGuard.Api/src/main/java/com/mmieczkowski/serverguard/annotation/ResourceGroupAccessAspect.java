package com.mmieczkowski.serverguard.annotation;

import com.mmieczkowski.serverguard.exception.NoAccessToResourceGroupException;
import com.mmieczkowski.serverguard.resourcegroup.model.ResourceGroupUserRole;
import com.mmieczkowski.serverguard.service.ResourceGroupAccessPolicyEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
public class ResourceGroupAccessAspect {
    private final ResourceGroupAccessPolicyEvaluator resourceGroupAccessPolicyEvaluator;

    public ResourceGroupAccessAspect(ResourceGroupAccessPolicyEvaluator resourceGroupAccessPolicyEvaluator) {
        this.resourceGroupAccessPolicyEvaluator = resourceGroupAccessPolicyEvaluator;
    }

    @Around("@annotation(com.mmieczkowski.serverguard.annotation.ResourceGroupAccess)")
    public Object checkResourceGroupAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        UUID resourceGroupId = getResourceGroupId(joinPoint, methodSignature);
        if(resourceGroupId == null){
            throw new IllegalArgumentException("ResourceGroupId not found");
        }
        boolean hasAccess = resourceGroupAccessPolicyEvaluator.evaluate(resourceGroupId, getRoles(methodSignature));
        if(!hasAccess){
            throw new NoAccessToResourceGroupException();
        }
        return joinPoint.proceed();
    }

    @Nullable
    private static UUID getResourceGroupId(ProceedingJoinPoint joinPoint, MethodSignature methodSignature) {
        Method method = methodSignature.getMethod();
        ResourceGroupAccess annotation = method.getAnnotation(ResourceGroupAccess.class);
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        UUID resourceGroupId = null;
        for(int i = 0; i < parameterNames.length; i++){
            if(parameterValues[i] instanceof UUID && parameterNames[i].equals(annotation.value())){
                resourceGroupId = (UUID) parameterValues[i];
            }

        }
        return resourceGroupId;
    }

    private static ResourceGroupUserRole[] getRoles(MethodSignature methodSignature) {
        Method method = methodSignature.getMethod();
        ResourceGroupAccess annotation = method.getAnnotation(ResourceGroupAccess.class);
        return annotation.roles();
    }
}
