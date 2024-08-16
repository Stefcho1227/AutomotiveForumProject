package com.example.forumproject.controllers.mvc;

import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.TagService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {
    private final UserService userService;
    private final PostService postService;
    private final TagService tagService;

    @Autowired
    public GlobalModelAttributes(UserService userService, PostService postService, TagService tagService) {
        this.userService = userService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            return user.getRole().getRoleName().equalsIgnoreCase("admin");
        }
        return false;
    }

    @ModelAttribute("userCount")
    public long populateUserCount() {
        return userService.getAllUsers().size();
    }

    @ModelAttribute("postCount")
    public int populatePostCount() {
        return postService.getPostCount();
    }

    @ModelAttribute("tags")
    public List<Tag> populateTags() {
        return tagService.getAllTags(new User());
    }
}
