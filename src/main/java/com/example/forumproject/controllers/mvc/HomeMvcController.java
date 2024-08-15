package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthenticationFailureException;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.UserMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.UserDto;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    public HomeMvcController(PostService postService, UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
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
    @GetMapping("/profile/edit")
    public String showEditProfilePage(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            UserDto userDto = userMapper.toDto(user);
            model.addAttribute("userDto", userDto);
            return "EditProfileView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


    @PostMapping("/profile/edit")
    public String updateUserProfile(@Valid @ModelAttribute("userDto") UserDto userDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {
        try {
            User authenticatedUser = authenticationHelper.tryGetCurrentUser(session);

            if (bindingResult.hasErrors()) {
                return "EditProfileView";
            }
            if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "password_error", "Passwords must match!");
                return "EditProfileView";
            }

            try {
                User userToUpdate = userMapper.fromDto(authenticatedUser.getId(), userDto);
                userService.updateUser(userToUpdate, authenticatedUser.getId());
                return "redirect:/profile";
            } catch (EntityNotFoundException e) {
                model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
                model.addAttribute("error", e.getMessage());
                return "ErrorView";
            } catch (DuplicateEntityException e) {
                if (e.getMessage().contains("email")) {
                    bindingResult.rejectValue("email", "email_error", e.getMessage());
                } else {
                    bindingResult.rejectValue("username", "username_error", e.getMessage());
                }
                return "EditProfileView";
            } catch (AuthorizationException e) {
                model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
                model.addAttribute("error", e.getMessage());
                return "ErrorView";
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }
}
