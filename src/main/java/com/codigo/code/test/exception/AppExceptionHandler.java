package com.codigo.code.test.exception;

import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.utils.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        Response response = ResponseBuilder.newBuilder()
                .withMessage(messages.get(0))
                .withHttpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response> handleNoResourceFound(NoResourceFoundException ex) {
        Response response = ResponseBuilder.newBuilder()
                .withMessage(ex.getMessage())
                .withHttpStatus(HttpStatus.NOT_FOUND)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Response response = ResponseBuilder.newBuilder()
                .withMessage(ex.getMessage())
                .withHttpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
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
