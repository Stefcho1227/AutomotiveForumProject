package com.example.forumproject.controllers.mvc;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class AdminMvcController {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public AdminMvcController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

@ModelAttribute("users")
public List<User> populateUsers() {
        return userService.getAllUsers();
}
    @GetMapping("/admin")
    public String getAdminPanelView(Model model,
                                    @RequestParam(value = "title", defaultValue = "") String title,
                                    @RequestParam(value = "tag", defaultValue = "") String tag,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                    @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        if (model.getAttribute("isAdmin") != null) {
            if ((boolean) model.getAttribute("isAdmin")) {
                Page<Post> posts = postService.getAllPosts(title, tag, page, size, sortBy, direction);
                model.addAttribute("posts", posts);
                return "AdminPanelView";
            }
        }
        return "redirect:/";
    }
}
