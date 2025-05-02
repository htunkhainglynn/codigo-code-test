package com.codigo.code.test.dto;

import com.codigo.code.test.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String username;
    private String password;
    private String status;
    private List<Role> roles;
}
