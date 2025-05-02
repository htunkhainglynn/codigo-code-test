package com.codigo.code.test.dto.request;

import lombok.NonNull;

public record AuthRequest(

        @NonNull
        String username,

        @NonNull
        String password
) {
}
