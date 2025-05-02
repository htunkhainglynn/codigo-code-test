package com.codigo.code.test.service.impl;

import com.codigo.code.test.service.MailingService;
import org.springframework.stereotype.Service;

@Service
public class MailingServiceImpl implements MailingService {
    @Override
    public void sendPasswordResetEmail(String email, String resetLink) {
        String subject = "Password Reset Request";
        String body = "We received a request to reset your password. Click the link below to reset it:\n\n" +
                resetLink + "\n\n" +
                "If you didn't request this, please ignore this email.";
    }

    @Override
    public boolean sendVerifyEmail(String email, String verificationLink) {
        return true;
    }
}
