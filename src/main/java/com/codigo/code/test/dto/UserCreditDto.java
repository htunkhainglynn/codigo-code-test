package com.codigo.code.test.dto;

public record UserCreditDto(
        String countryCode,
        int remainingCredit,

        int expiredDateCount
) {
}
