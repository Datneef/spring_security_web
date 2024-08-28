package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  Model model) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("emailError", "Email đã được sử dụng");
            return "register";
        }

        if (userService.findByPhone(user.getPhone()) != null) {
            model.addAttribute("phoneError", "Số điện thoại đã được sử dụng");
            return "register";
        }

        User registeredUser = userService.registerUser(user);

        String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        userService.sendVerificationEmail(registeredUser, siteURL);

        model.addAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
        return "register";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        if (userService.verify(code)) {
            model.addAttribute("message", "Xác thực thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else {
            model.addAttribute("message", "Mã xác thực không hợp lệ hoặc đã được sử dụng.");
        }
        return "login";
    }
}
