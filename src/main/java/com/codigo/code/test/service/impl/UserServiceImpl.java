package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.PackageDto;
import com.codigo.code.test.dto.UserCreditDto;
import com.codigo.code.test.dto.UserProfileDto;
import com.codigo.code.test.dto.request.ResetPwRequest;
import com.codigo.code.test.dto.response.CourseDto;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.BookingStatus;
import com.codigo.code.test.entity.Package;
import com.codigo.code.test.entity.User;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.UserRepository;
import com.codigo.code.test.security.JwtTokenProvider;
import com.codigo.code.test.service.UserService;
import com.codigo.code.test.utils.ResponseBuilder;
import com.codigo.code.test.utils.SecurityContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final SecurityContextUtils securityContext;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;
    private final MailingServiceImpl mailingService;

    @Value("${user-pw-reset-link}")
    private String userPwResetLink;

    @Transactional(readOnly = true)
    @Override
    public Response getProfile() {
        try {
            User user = userRepo.getReferenceByUsername(securityContext.getUsername())
                    .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

            List<PackageDto> packageDtos = user.getUserPackages().stream()
                    .map(userPackage -> {
                        Package pkg = userPackage.getPkg();
                        return new PackageDto(pkg.getId(), pkg.getName(), pkg.getCredit(), pkg.getDescription(), pkg.getExpiredDateCount(), pkg.getCountry().getCountryCode());
                    })
                    .toList();

            List<UserCreditDto> userCreditDtos = user.getUserCredits().stream()
                    .map(userCredit -> new UserCreditDto(userCredit.getCountry().getCountryCode(), userCredit.getRemainingCredits(), userCredit.getExpiredDate())).toList();

            List<CourseDto> courseDtos = user.getBookings().stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.BOOKED || booking.getStatus() == BookingStatus.PENDING)
                    .map(booking -> new CourseDto(booking.getCourse()))
                    .toList();

            UserProfileDto userProfileDto = new UserProfileDto(user.getName(), user.getUsername(), packageDtos, userCreditDtos, courseDtos);

            return ResponseBuilder
                    .newBuilder()
                    .withData(userProfileDto)
                    .withMessage("User profile retrieved successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving user profile: {}", e.getMessage());
            throw new ApplicationException("Error retrieving user profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Response changePassword(String newPassword) {
        try {
            String currentPassword = userRepo.getPassword(securityContext.getUsername());
            checkCurrentAndNewPasswordAreDiff(currentPassword, newPassword);

            User user = userRepo.getReferenceByUsername(securityContext.getUsername())
                    .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

            user.setPassword(encoder.encode(newPassword));

            userRepo.save(user);

            return ResponseBuilder
                    .newBuilder()
                    .withData(null)
                    .withMessage("User profile retrieved successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            throw new ApplicationException("Error changing password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Response resetPassword(String token, ResetPwRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ApplicationException("Invalid token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtTokenProvider.getUsername(token);
        User user = userRepo.getReferenceByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

        if (encoder.matches(request.newPassword(), user.getPassword())) {
            throw new ApplicationException("New password cannot be the same as the current password", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(encoder.encode(request.newPassword()));

        try {
            userRepo.save(user);
            log.info("Password reset successfully for user: {}", username);

            return ResponseBuilder
                    .newBuilder()
                    .withData(null)
                    .withMessage("Password reset successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            throw new ApplicationException("Error resetting password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response sentEmailPwReset(String email) {
        User user = userRepo.getReferenceByUsername(securityContext.getUsername())
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
        log.info("User {}", user);

        String resetToken = jwtTokenProvider.createToken(securityContext.getUsername(), getDeviceId());
        String resetLink = userPwResetLink + "?token=" + resetToken;

        mailingService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return ResponseBuilder.newBuilder()
                .withData(resetLink)
                .withKey("pw_reset_link")
                .withMessage("Password reset link sent successfully")
                .build();
    }

    private void checkCurrentAndNewPasswordAreDiff(String currentPassword, String newPassword) {
        if (encoder.matches(newPassword, currentPassword)) {
            throw new ApplicationException("New password cannot be the same as the current password", HttpStatus.BAD_REQUEST);
        }
    }

    private String getDeviceId() {
        return request.getHeader("deviceId");
    }
}
