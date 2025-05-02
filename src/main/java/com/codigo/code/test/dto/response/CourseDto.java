package com.codigo.code.test.dto.response;

import com.codigo.code.test.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private int credit;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slot;
    private String countryCode;

    public CourseDto(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.credit = course.getCredit();
        this.endDate = course.getEndDate();
        this.startTime = course.getStartTime();
        this.endTime = course.getEndTime();
        this.slot = course.getSlot();
        this.countryCode = course.getCountry().getCountryCode();
    }
}
