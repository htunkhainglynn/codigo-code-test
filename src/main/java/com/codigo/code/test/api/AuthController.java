package com.codigo.code.test.api;

import com.codigo.code.test.dto.request.AuthRequest;
import com.codigo.code.test.dto.request.RegisterRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Response authenticate(@RequestBody @Validated AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/register")
    public Response register(@RequestBody @Validated RegisterRequest authRequest) {
        return authService.register(authRequest);
    }

    @PostMapping("/logout")
    public Response logout() {
        return authService.logout();
    }

}
