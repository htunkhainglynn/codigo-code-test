package com.codigo.code.test.dto;

import lombok.Builder;

@Builder
public record PackageDto(
        Long id,
        String name,
        int credit,
        String description,
        int expiredDateCount,
        String countryCode
) {
}
