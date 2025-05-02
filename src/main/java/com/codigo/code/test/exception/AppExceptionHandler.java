package com.codigo.code.test.exception;

import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.utils.ResponseBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public Response handleUserException(ApplicationException e) {
        return ResponseBuilder.newBuilder()
                .withMessage(e.getMessage())
                .withHttpStatus(e.getHttpStatus())
                .build();
    }
}
