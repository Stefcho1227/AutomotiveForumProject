package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthenticationFailureException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.UserMapper;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.LoginDto;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthMvcController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AuthMvcController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto loginDto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }

        try {
            User user = authenticationHelper.throwIfWrongAuthentication(loginDto.getUsername(), loginDto.getPassword());
            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getId());
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new UserInDto());
        return "SignUpView";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") UserInDto registerDto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "SignUpView";
        }
        if (!registerDto.getConfirmPassword().equals(registerDto.getPassword())) {
            bindingResult.rejectValue("password", "confirm_password_error", "Passwords should match.");
            return "SignUpView";
        }
        try {
            User user = userMapper.fromDto(registerDto);
            userService.createUser(user);
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "duplicate_user", e.getMessage());
            return "SignUpView";
        }
        return "redirect:/";
    }
}
