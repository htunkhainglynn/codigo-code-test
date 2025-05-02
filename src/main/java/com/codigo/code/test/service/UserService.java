package com.codigo.code.test.service;

import com.codigo.code.test.dto.request.ResetPwRequest;
import com.codigo.code.test.dto.response.Response;

public interface UserService {
    Response getProfile();

    Response changePassword(String password);

    Response resetPassword(String token, ResetPwRequest request);

    Response sentEmailPwReset(String email);
}
