package com.codigo.code.test.dto;

import java.time.LocalDate;

public record UserCreditDto(
        String countryCode,
        int remainingCredit,

        LocalDate expireDate
) {
}
