package com.codigo.code.test.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T map(Object source, Class<T> targetType) {
        return objectMapper.convertValue(source, targetType);
    }

    // for complex types
    public static <T> T map(Object source, TypeReference<T> targetType) {
        return objectMapper.convertValue(source, targetType);
    }

}


