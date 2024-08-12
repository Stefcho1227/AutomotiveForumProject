package com.example.forumproject.controllers.mvc;

import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public GlobalModelAttributes(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("userCount")
    public int populateUserCount(HttpSession session) {
        return (int) userService.getAllUsers().stream().count();
    }

    @ModelAttribute("postCount")
    public int populatePostCount(HttpSession session) {
        return postService.getPostCount();
    }
}
