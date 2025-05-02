package com.codigo.code.test.security;

import com.codigo.code.test.config.SecurityProperties;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.enums.AccountStatus;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.service.impl.RedisService;
import com.codigo.code.test.utils.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
        log.info("request uri: {}", request.getRequestURI());

        if (securityProperties.getUnauthorizedUrls().stream().anyMatch(url -> request.getRequestURI().equals(url))) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String status = checkStatusInRedis(jwtTokenProvider.getUsername(token), jwtTokenProvider.getDeviceId(token));

            if (AccountStatus.INACTIVE.getStatus().equals(status)) {
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, "Account is inactive", HttpStatus.UNAUTHORIZED);
                return;
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } else {
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
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

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus statusCode) throws IOException {
        response.setStatus(statusCode.value());
        response.setContentType("application/json");
        Response body = ResponseBuilder
                .newBuilder()
                .withMessage(message)
                .withHttpStatus(statusCode)
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}


