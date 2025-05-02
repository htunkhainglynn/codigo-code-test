package com.codigo.code.test.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Response(
        Object data,

        String message,

        String status,

        String timeStamp
) {}