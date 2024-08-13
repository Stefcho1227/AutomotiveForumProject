package com.example.forumproject.controllers.mvc;

import com.example.forumproject.services.contracts.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;

    public HomeMvcController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String ShowHomeView(Model model) {
        model.addAttribute("mostLikedPosts", postService.getTenMostLikedPosts());
        model.addAttribute("mostCommentedPosts", postService.getTenMostCommentedPosts());
        model.addAttribute("mostRecentPosts", postService.getTenMostRecentPosts());
        return "HomeView";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "AboutUs";
    }
}
