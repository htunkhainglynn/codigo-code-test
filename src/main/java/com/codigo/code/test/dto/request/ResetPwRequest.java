package com.codigo.code.test.dto.request;

import jakarta.validation.constraints.NotNull;

public record ResetPwRequest(

        @NotNull(message = "Current cannot be null")
        String currentPassword,

        @NotNull(message = "New cannot be null")
        String newPassword
) {
}
