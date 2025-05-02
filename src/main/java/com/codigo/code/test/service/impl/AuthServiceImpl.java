package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.UserDto;
import com.codigo.code.test.dto.request.AuthRequest;
import com.codigo.code.test.dto.request.RegisterRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.User;
import com.codigo.code.test.enums.AccountStatus;
import com.codigo.code.test.enums.Role;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.UserRepository;
import com.codigo.code.test.security.JwtTokenProvider;
import com.codigo.code.test.service.AuthService;
import com.codigo.code.test.service.MyUserDetails;
import com.codigo.code.test.utils.ObjectMapperUtil;
import com.codigo.code.test.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final MailingServiceImpl mailingService;

    @Transactional(readOnly = true)
    @Override
    public Response authenticate(AuthRequest authRequest) {
        String username = authRequest.username();
        String password = authRequest.password();

        loginUserExistCheck(authRequest.username());

        try {
            Optional<User> userReference = this.userRepository.getReferenceByUsername(username);
            if (userReference.isEmpty()) {
                throw new ApplicationException("Invalid username/email or password supplied", HttpStatus.UNAUTHORIZED);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authentication);

            String token = jwtTokenProvider.createToken(username, getDeviceId());

            saveUserStatusInRedis(username);

            return ResponseBuilder
                    .newBuilder()
                    .withMessage("User authenticated successfully")
                    .withData(token)
                    .withKey("token")
                    .build();
        } catch (AuthenticationException e) {
            throw new ApplicationException("Invalid username/email or password supplied", HttpStatus.UNAUTHORIZED);
        }

    }

    private void saveUserStatusInRedis(String username) {
        String redisKey = username + ":" + getDeviceId();
        redisService.set(redisKey, AccountStatus.ACTIVE.getStatus(), 60);
    }

    @Transactional
    @Override
    public Response register(RegisterRequest registerRequest) {
        registerUserExistCheck(registerRequest.username());

        if (mailingService.sendVerifyEmail(registerRequest.email(), "link")) {
            log.info("Verification email sent to {}", registerRequest.email());
        } else {
            throw new ApplicationException("Error sending verification email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserDto userDto = UserDto.builder()
                .name(registerRequest.name())
                .username(registerRequest.username())
                .status(AccountStatus.ACTIVE.getStatus())
                .roles(List.of(Role.USER))
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password())).build();

        User user = saveUser(userDto);
        log.info("Saved user {}", user);

        return ResponseBuilder
                .newBuilder()
                .withMessage("User registered successfully")
                .withData(user)
                .build();
    }

    @Override
    public Response logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String deviceId = getDeviceId();
        String redisKey = username + ":" + deviceId;

        redisService.set(redisKey, AccountStatus.INACTIVE.getStatus(), 60);

        return ResponseBuilder
                .newBuilder()
                .withMessage("User logged out successfully")
                .build();
    }

    private User saveUser(UserDto userDto) {
        User user = new User(userDto);
        try{
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ApplicationException("Error saving user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void registerUserExistCheck(String username) {
        Optional<User> optionalUser = getUserByUsername(username);
        if (optionalUser.isPresent()) {
            throw new ApplicationException("User already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private void loginUserExistCheck(String username) {
        Optional<User> optionalUser = getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ApplicationException("Invalid username/email or password supplied", HttpStatus.BAD_REQUEST);
        }
    }

    private Optional<User> getUserByUsername(String username) {
        try {
            return userRepository.getReferenceByUsername(username);
        } catch (Exception e) {
            throw new ApplicationException("Error checking user existence", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getDeviceId() {
        return request.getHeader("deviceId");
    }
}
