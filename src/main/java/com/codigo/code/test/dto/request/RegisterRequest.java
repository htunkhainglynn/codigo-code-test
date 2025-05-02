package com.codigo.code.test.dto.request;

import lombok.NonNull;

public record RegisterRequest(
        @NonNull
        String username,

        @NonNull
        String password,

        @NonNull
        String name
) { }
