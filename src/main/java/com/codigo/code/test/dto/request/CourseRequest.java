package com.codigo.code.test.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CourseRequest(
        @NotNull(message = "Name cannot be null")
        String name,

        @NotNull(message = "Description cannot be null")
        String description,

        @Min(value = 1, message = "Credit must be greater than 0")
        int credit,

        @NotNull(message = "End date cannot be null")
        LocalDate endDate,

        @NotNull(message = "Start time cannot be null")
        LocalTime startTime,

        @NotNull(message = "End time cannot be null")
        LocalTime endTime,

        @Min(value = 1, message = "Slot must be greater than 0")
        int slot,

        @NotNull(message = "Country code cannot be null")
        String countryCode
) {
}
