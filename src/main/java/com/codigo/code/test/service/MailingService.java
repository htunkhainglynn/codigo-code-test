package com.codigo.code.test.service;

public interface MailingService {
    void sendPasswordResetEmail(String email, String resetLink);
}
