package com.codigo.code.test.dto;


import com.codigo.code.test.dto.response.CourseDto;

import java.util.List;

public record UserProfileDto(
        String name,
        String username,

        List<PackageDto> userPackages,

        List<UserCreditDto> userCredits,

        List<CourseDto> courses

) {
}
