package com.codigo.code.test.exception;

import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.utils.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

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

    public static void sendErrorResponse(HttpServletResponse response, String message, HttpStatus statusCode) throws IOException {
        response.setStatus(statusCode.value());
        response.setContentType("application/json");
        Response body = ResponseBuilder
                .newBuilder()
                .withMessage(message)
                .withHttpStatus(statusCode)
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
