package com.example.forumproject.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final HttpSession httpSession;

    public HomeMvcController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }


    @GetMapping
    public String ShowHomeView() {
        return "HomeView";
    }
    @GetMapping("/about")
    public String showAboutPage() {
        return "AboutUs";
    }
}
