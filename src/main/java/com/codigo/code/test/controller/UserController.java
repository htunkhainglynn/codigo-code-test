package com.codigo.code.test.controller;

import com.codigo.code.test.dto.request.ResetPwRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Response> getProfile() {
        return ok(userService.getProfile());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response> changePassword(@RequestBody String password) {
        return ok(userService.changePassword(password));
    }

    @PostMapping("/pw-reset-request")
    public ResponseEntity<Response> sentEmailPwReset(@RequestBody String email) {
        return ok(userService.sentEmailPwReset(email));
    }

    @PostMapping("/reset-pw/{token}")
    public ResponseEntity<Response> resetPassword(@PathVariable String token,
                                                  @RequestBody ResetPwRequest request) {
        return ok(userService.resetPassword(token, request));
    }
}
