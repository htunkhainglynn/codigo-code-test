package com.codigo.code.test.controller;

import com.codigo.code.test.dto.request.AuthRequest;
import com.codigo.code.test.dto.request.RegisterRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response> authenticate(@RequestBody @Validated AuthRequest authRequest) {
        return ok(authService.authenticate(authRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Validated RegisterRequest authRequest) {
        return ok(authService.register(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout() {
        return ok(authService.logout());
    }

}
