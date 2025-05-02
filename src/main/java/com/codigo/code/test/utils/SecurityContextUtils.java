package com.codigo.code.test.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextUtils {

    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
