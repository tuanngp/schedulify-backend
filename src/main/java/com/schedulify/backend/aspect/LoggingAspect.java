package com.schedulify.backend.aspect;

import com.schedulify.backend.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.schedulify.backend.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LogUtils.setTraceId();
        
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Start {} - Method: {}.{}, Args: {}", 
                LogUtils.getTraceId(), 
                className, 
                methodName, 
                LogUtils.formatArgs(joinPoint.getArgs())
            );
            
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("End {} - Method: {}.{}, ExecutionTime: {}ms, Result: {}", 
                LogUtils.getTraceId(),
                className, 
                methodName, 
                executionTime,
                result
            );
            
            return result;
        } catch (Exception e) {
            log.error("Error {} - Method: {}.{}, Exception: {}", 
                LogUtils.getTraceId(),
                className, 
                methodName, 
                e.getMessage(),
                e
            );
            throw e;
        } finally {
            LogUtils.clearTraceId();
        }
    }
}