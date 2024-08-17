package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthenticationFailureException;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.UserMapper;
import com.example.forumproject.helpers.specifications.PostMvcSpecification;
import com.example.forumproject.helpers.specifications.PostSpecification;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.UserDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


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
    public String ShowHomeView(Model model, HttpSession session,
                               @RequestParam(value = "title", defaultValue = "") String title,
                               @RequestParam(value = "tag", defaultValue = "") String tag,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                               @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        model.addAttribute("mostLikedPosts", postService.getTenMostLikedPosts());
        model.addAttribute("mostCommentedPosts", postService.getTenMostCommentedPosts());
        model.addAttribute("mostRecentPosts", postService.getTenMostRecentPosts());
        if (session.getAttribute("currentUser") != null) {
            Page<Post> posts = postService.getAllPosts(title, tag, page, size, sortBy, direction);
            model.addAttribute("posts", posts);
        }
        return "HomeView";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "AboutUs";
    }

    @GetMapping("/profile")
    public String showProfileView(Model model, HttpSession session) {
        int userId = (Integer) session.getAttribute("userId");
        if (userId <= 0) {
            return "redirect:/auth/login";
        }
        User user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException("user", userId));
        if (user == null) {
            return "ErrorView";
        }
        int totalLikes = postService.getUserPosts(userId)
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
    public String updateUserProfile(@ModelAttribute("userDto") UserDto userDto,
                                    @RequestParam("profilePhoto") MultipartFile profilePhoto,
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
                String oldPhoto = userService.getByUsername(userDto.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException("User not found")).getProfilePictureUrl();
                String fileName = "";
                String directory = "";
                if (profilePhoto != null && !profilePhoto.isEmpty()) {
                    fileName = userDto.getUsername() + "_" + System.currentTimeMillis() + "_" + profilePhoto.getOriginalFilename();
                    byte[] bytes = profilePhoto.getBytes();
                    Path path = Paths.get("src","main","resources", "static", "assets", "profile");
                    File dir = new File(path + File.separator);
                    directory = dir.toString();
                    if (!dir.exists())
                        dir.mkdirs();

                    File serverFile = new File(dir.getAbsolutePath()
                            + File.separator + fileName);
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));
                    stream.write(bytes);
                    stream.close();

/*                    if (oldPhoto != null) {
                        Files.deleteIfExists(Paths.get("/assets/profile/" + oldPhoto));
                    }*/
                }

                User userToUpdate = userMapper.fromDto(authenticatedUser.getId(), userDto);
                directory = directory.replace("src\\main\\resources\\static", "").replace("\\", "/");
                userToUpdate.setProfilePictureUrl(directory + "/" + fileName);
                userService.updateUser(userToUpdate, authenticatedUser.getId());
                return "redirect:/profile";

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "Failed to upload '" + profilePhoto.getOriginalFilename() + "'");
                return "EditProfileView";
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
