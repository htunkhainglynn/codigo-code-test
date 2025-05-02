package com.codigo.code.test.security;

import com.codigo.code.test.config.SecurityProperties;
import com.codigo.code.test.enums.AccountStatus;
import com.codigo.code.test.exception.AppExceptionHandler;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.service.impl.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws
            ServletException, IOException {

        log.info("unauthorized urls: {}", securityProperties.getUnauthorizedUrls().toString());

        if (securityProperties.getUnauthorizedUrls().stream().anyMatch(url -> request.getRequestURI().equals(url))) {
            log.info("Request URI: {} is in the unauthorized urls list", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);
        log.info("Token: {}", token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("Token is valid");

            String status = checkStatusInRedis(jwtTokenProvider.getUsername(token), jwtTokenProvider.getDeviceId(token));
            log.info("Status from Redis: {}", status);

            if (AccountStatus.INACTIVE.getStatus().equals(status)) {
                SecurityContextHolder.clearContext();
                AppExceptionHandler.sendErrorResponse(response, "Account is inactive", HttpStatus.UNAUTHORIZED);
                return;
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Authentication successful for user: {}", jwtTokenProvider.getUsername(token));
        } else {
            SecurityContextHolder.clearContext();
            AppExceptionHandler.sendErrorResponse(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String checkStatusInRedis(String username, String deviceId) {
        try {
            String redisKey = username + ":" + deviceId;
            return redisService.get(redisKey);
        } catch (Exception e) {
            throw new ApplicationException("Error checking status in Redis", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


