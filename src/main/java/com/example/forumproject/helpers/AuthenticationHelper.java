package com.example.forumproject.helpers;

import com.example.forumproject.exceptions.AuthenticationFailureException;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.BlockedException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String MODIFY_ERROR_MESSAGE = "Only admin or post creator can modify a post.";
    public static final String WRONG_USERNAME_OR_PASSWORD = "Wrong username or password";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        if (userInfo == null || !userInfo.startsWith("Basic ")) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        try {
            String base64Credentials = userInfo.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }

            String username = values[0];
            String password = values[1];
            Optional<User> userOptional = userService.getByUsername(username);
            User user = userOptional.orElseThrow(() -> new AuthorizationException(INVALID_AUTHENTICATION_ERROR));
            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            throwIfUserIsBlocked(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User tryGetCurrentUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            throw new AuthorizationException("Invalid authentication. Please log in.");
        }

        return currentUser;
    }

    public static void throwIfUserIsBlocked(User user) {
        if (user.getIsBlocked()) {
            throw new BlockedException("Username", user.getUsername());
        }
    }

    public User throwIfWrongAuthentication(String username, String password) {
        Optional<User> user = userService.getByUsername(username);
        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            throw new AuthenticationFailureException("Wrong username or password.");
        }
        return user.get();
    }
}
