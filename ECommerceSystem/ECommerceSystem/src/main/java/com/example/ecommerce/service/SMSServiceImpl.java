package com.example.ecommerce.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSServiceImpl implements SMSService {

    @Value("${twilio.phone.number}")
    private String fromNumber;

    @Override
    public void sendResetPasswordSMS(String to, String resetPasswordLink) {
        String messageContent = "Để đặt lại mật khẩu của bạn, vui lòng nhấp vào liên kết sau: " + resetPasswordLink;

        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                messageContent
        ).create();
    }
}
