package com.example.forumproject.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    @GetMapping
    public String ShowHomeView() {
        return "HomeView";
    }
    @GetMapping("/about")
    public String showAboutPage() {
        return "AboutUs";
    }
}
