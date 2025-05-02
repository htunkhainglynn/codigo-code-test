package com.codigo.code.test.dto.request;

public record ResetPwRequest(
        String currentPassword,
        String newPassword
) {
}
