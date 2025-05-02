package com.codigo.code.test.service;

import com.codigo.code.test.dto.request.AuthRequest;
import com.codigo.code.test.dto.request.RegisterRequest;
import com.codigo.code.test.dto.response.Response;

public interface AuthService {
    Response authenticate(AuthRequest request);

    Response register(RegisterRequest authRequest);

    Response logout();
}
