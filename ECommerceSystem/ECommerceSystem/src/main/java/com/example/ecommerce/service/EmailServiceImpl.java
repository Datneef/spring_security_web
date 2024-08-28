package com.example.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;


import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("your_email@gmail.com", "E-commerce Support");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(String to, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        String subject = "Yêu cầu đặt lại mật khẩu";
        String senderName = "E-commerce Support";
        String content = "<p>Chào bạn,</p>";
        content += "<p>Bạn đã yêu cầu đặt lại mật khẩu của mình.</p>";
        content += "<p>Nhấp vào liên kết dưới đây để đặt lại mật khẩu của bạn:</p>";
        content += "<p><a href=\"" + resetPasswordLink + "\">ĐẶT LẠI MẬT KHẨU</a></p>";
        content += "<br>";
        content += "<p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>";
        content += "<p>Trân trọng,<br>E-commerce Team</p>";

        sendEmail(to, subject, content);
    }
}
