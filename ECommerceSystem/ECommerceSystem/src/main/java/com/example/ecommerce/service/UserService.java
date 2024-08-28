package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

public interface UserService {
    User registerUser(User user);
    void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;
    boolean verify(String verificationCode);
    User findByEmail(String email);
    User findByPhone(String phone);
    void updateResetPasswordToken(String token, String email);
    User getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);

    void changeUserPassword(Long userId, String newPassword);
    PasswordEncoder getPasswordEncoder();
}
