package com.codigo.code.test.dto.request;

import jakarta.validation.constraints.Min;

public record PackagePurchaseRequest(

        @Min(value = 1, message = "Course ID must be greater than 0")
        long id
) {
}
