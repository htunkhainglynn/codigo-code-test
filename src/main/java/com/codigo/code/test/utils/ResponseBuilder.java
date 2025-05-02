package com.codigo.code.test.utils;

import com.codigo.code.test.dto.response.Response;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ResponseBuilder<T> {

    private T data;
    private String key;
    private String message = "Success";

    private String httpStatus = HttpStatus.OK.toString();

    private ResponseBuilder() {}

    public static <T> ResponseBuilder<T> newBuilder() {
        return new ResponseBuilder<>();
    }

    public ResponseBuilder<T> withData(T data) {
        this.data = data;
        return this;
    }

    public ResponseBuilder<T> withKey(String key) {
        this.key = key;
        return this;
    }

    public ResponseBuilder<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseBuilder<T> withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus.toString();
        return this;
    }

    public Response build() {
        Object payload = (key != null) ? Map.of(key, data) : data;
        return Response.builder()
                .data(payload)
                .message(message)
                .status(httpStatus)
                .timeStamp(getCurrentFormattedTime())
                .build();
    }

    private String getCurrentFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
        return sdf.format(new Date());
    }
}

