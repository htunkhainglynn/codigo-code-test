package com.codigo.code.test.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record RegisterRequest(
        @NotNull(message = "Username cannot be null")
        String username,

        @NotNull(message = "Email cannot be null")
        String email,

        @NotNull(message = "Password cannot be null")
        String password,

        @NotNull(message = "Name cannot be null")
        String name
) { }
