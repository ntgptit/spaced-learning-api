package com.spacedlearning.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final long SLOW_THRESHOLD_MS = 500;

    @Pointcut("servicePointcut() || repositoryPointcut() || controllerPointcut()")
    public void applicationPointcut() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @AfterThrowing(pointcut = "applicationPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL");

        if (LoggingAspect.log.isDebugEnabled()) {
            LoggingAspect.log.debug("Exception stacktrace: ", e); 
        }
    }

    @Around("applicationPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logMethodEntry(joinPoint);

        final var start = System.currentTimeMillis();
        try {
            final var result = joinPoint.proceed();
            final var executionTime = System.currentTimeMillis() - start;

            logMethodExit(joinPoint, result, executionTime);

            if (executionTime > LoggingAspect.SLOW_THRESHOLD_MS) {
                LoggingAspect.log.warn("Slow execution: {}.{}() took {} ms",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), executionTime);
            }

            return result;

        } catch (final IllegalArgumentException ex) {
            LoggingAspect.log.error("Illegal argument: {} in {}.{}()",
                    ex.getMessage(), joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw ex;
        }
    }

    private void logMethodEntry(ProceedingJoinPoint joinPoint) {
        if (LoggingAspect.log.isDebugEnabled()) {
            LoggingAspect.log.debug("Enter: {}.{}() with arguments = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs());
        }
    }

    private void logMethodExit(ProceedingJoinPoint joinPoint, Object result, long executionTime) {
        if (LoggingAspect.log.isDebugEnabled()) {
            LoggingAspect.log.debug("Exit: {}.{}() with result = {} (execution time: {} ms)",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result, executionTime);
        }
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryPointcut() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
    }
}
