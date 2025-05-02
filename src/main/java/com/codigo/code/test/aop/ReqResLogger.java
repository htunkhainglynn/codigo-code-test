package com.codigo.code.test.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ReqResLogger {

    private static final Logger logger = LoggerFactory.getLogger(ReqResLogger.class);

    private final HttpServletRequest request;

    @Pointcut("execution(* com.codigo.code.test.controller..*(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logRequest() {
        logger.info("Request URI: {}", request.getRequestURI());
        logger.info("HTTP Method: {}", request.getMethod());
        logger.info("Request Parameters: {}", request.getParameterMap().toString());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logResponse(Object result) {
        logger.info("Response: {}", result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logException(Exception ex) {
        logger.error("Exception: {}", ex.getMessage());
    }
}
