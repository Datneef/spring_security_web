package com.example.ecommerce.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException;
    void sendResetPasswordEmail(String to, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException;
}
