package com.codigo.code.test.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record CourseRequest(
        String name,
        String description,
        int credit,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        int slot
) {
}
