package com.codigo.code.test.dto;


import java.util.List;

public record UserProfileDto(
        String name,
        String username,

        List<PackageDto> userPackages,

        List<UserCreditDto> userCredits

) {
}
