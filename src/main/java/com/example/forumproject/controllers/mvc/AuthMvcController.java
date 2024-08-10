package com.example.forumproject.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthMvcController {
    @GetMapping("/login")
    public String showLoginPage() {
        return "LoginView";
    }
}
