package com.example.ecommerce.service;

public interface SMSService {
    void sendResetPasswordSMS(String to, String resetPasswordLink);
}
