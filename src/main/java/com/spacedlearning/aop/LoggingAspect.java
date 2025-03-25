// File: src/main/java/com/spacedlearning/aop/LoggingAspect.java
package com.spacedlearning.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

	/**
	 * Combined pointcut for services, repositories, and controllers.
	 */
	@Pointcut("servicePointcut() || repositoryPointcut() || controllerPointcut()")
	public void applicationPointcut() {
		// Method is empty as this is just a pointcut
	}

	/**
	 * Pointcut for all controller methods.
	 */
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerPointcut() {
		// Method is empty as this is just a pointcut
	}

	/**
	 * Logs exceptions thrown by methods.
	 */
	@AfterThrowing(pointcut = "applicationPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");

		if (log.isDebugEnabled()) {
			log.debug("Exception stacktrace: ", e);
		}
	}

	/**
	 * Logs method execution time and parameters.
	 */
	@Around("applicationPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("Enter: {}.{}() with arguments = {}", joinPoint.getSignature().getDeclaringTypeName(),
					joinPoint.getSignature().getName(), joinPoint.getArgs());
		}

		try {
			final long start = System.currentTimeMillis();
			final Object result = joinPoint.proceed();
			final long executionTime = System.currentTimeMillis() - start;

			if (log.isDebugEnabled()) {
				log.debug("Exit: {}.{}() with result = {} (execution time: {} ms)",
						joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), result,
						executionTime);
			}

			// Log slow methods (over 500ms)
			if (executionTime > 500) {
				log.warn("Slow execution: {}.{}() took {} ms", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(), executionTime);
			}

			return result;
		} catch (final IllegalArgumentException e) {
			log.error("Illegal argument: {} in {}.{}()", e.getMessage(),
					joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
			throw e;
		}
	}

	/**
     * Pointcut for all repository methods.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryPointcut() {
        // Method is empty as this is just a pointcut
    }

	/**
	 * Pointcut for all service methods.
	 */
	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void servicePointcut() {
		// Method is empty as this is just a pointcut
	}
}