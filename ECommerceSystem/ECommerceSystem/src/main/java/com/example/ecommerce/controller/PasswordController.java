package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.EmailService;
import com.example.ecommerce.service.SMSService;
import com.example.ecommerce.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @GetMapping("/change_password")
    public String showChangePasswordForm() {
        return "change_password";
    }

    @PostMapping("/change_password")
    public String processChangePassword(@AuthenticationPrincipal com.example.ecommerce.security.CustomUserDetails loggedUser,
                                        @RequestParam("oldPassword") String oldPassword,
                                        @RequestParam("newPassword") String newPassword,
                                        Model model) {
        User user = userService.findByEmail(loggedUser.getUsername());

        if (!userService.getPasswordEncoder().matches(oldPassword, newPassword)) {
            model.addAttribute("error", "Mật khẩu cũ không chính xác");
            return "change_password";
        }

        if (newPassword.length() < 8 || !newPassword.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$&*]).{8,}$")) {
            model.addAttribute("error", "Mật khẩu mới không đáp ứng yêu cầu bảo mật");
            return "change_password";
        }

        userService.updatePassword(user, newPassword);
        model.addAttribute("message", "Đổi mật khẩu thành công");

        return "change_password";
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String emailOrPhone = request.getParameter("emailOrPhone");
        String token = RandomStringUtils.randomAlphanumeric(45);

        try {
            User user = null;
            String resetPasswordLink = getSiteURL(request) + "/reset_password?token=" + token;

            if (emailOrPhone.contains("@")) {
                user = userService.findByEmail(emailOrPhone);
                if (user == null) {
                    model.addAttribute("error", "Không tìm thấy người dùng với email này");
                    return "forgot_password";
                }
                userService.updateResetPasswordToken(token, emailOrPhone);
                emailService.sendResetPasswordEmail(user.getEmail(), resetPasswordLink);
                model.addAttribute("message", "Chúng tôi đã gửi liên kết đặt lại mật khẩu tới email của bạn.");
            } else {
                user = userService.findByPhone(emailOrPhone);
                if (user == null) {
                    model.addAttribute("error", "Không tìm thấy người dùng với số điện thoại này");
                    return "forgot_password";
                }
                userService.updateResetPasswordToken(token, user.getEmail());
                smsService.sendResetPasswordSMS(user.getPhone(), resetPasswordLink);
                model.addAttribute("message", "Chúng tôi đã gửi liên kết đặt lại mật khẩu tới số điện thoại của bạn.");
            }

        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
        }

        return "forgot_password";
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn");
            return "login";
        }

        if (newPassword.length() < 8 || !newPassword.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$&*]).{8,}$")) {
            model.addAttribute("error", "Mật khẩu mới không đáp ứng yêu cầu bảo mật");
            model.addAttribute("token", token);
            return "reset_password";
        }

        userService.updatePassword(user, newPassword);
        model.addAttribute("message", "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới.");
        return "login";
    }

    private String getSiteURL(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getServletPath(), "");
    }
}
