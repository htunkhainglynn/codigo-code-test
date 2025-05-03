package com.codigo.code.test.dto.request;

import lombok.NonNull;

public record CountryRequest(

        @NonNull
        String countryCode,

        @NonNull
        String countryName
) {
}
