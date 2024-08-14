package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;
    private final UserService userService;

    public HomeMvcController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
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
    @GetMapping("/profile")
    public String showProfileView(Model model, HttpSession session){
        int userId = (Integer) session.getAttribute("userId");
        if (userId <= 0) {
            return "redirect:/auth/login";
        }
        User user = userService.getUserById(userId).orElseThrow(()->new EntityNotFoundException("user", userId));
        if (user == null) {
            return "ErrorView";
        }
        int totalLikes =  postService.getUserPosts(userId)
                .stream()
                .mapToInt(Post::getLikesCount)
                .sum();
        model.addAttribute("user", user);
        model.addAttribute("posts", postService.getUserPosts(userId));
        model.addAttribute("postCount", postService.getUserPosts(userId).size());
        model.addAttribute("likesCount", totalLikes);
        return "ProfileView";
    }
}
