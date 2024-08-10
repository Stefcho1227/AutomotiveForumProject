package com.example.forumproject.controllers.rest;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.PhoneNumberMapper;
import com.example.forumproject.helpers.mapper.UserMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.models.dtos.in.PhoneNumberDto;
import com.example.forumproject.models.dtos.in.UserBlockDto;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserMapper userMapper;
    private PhoneNumberMapper phoneNumberMapper;
    private UserService userService;
    private AuthenticationHelper authenticationHelper;
    private PostService postService;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper,
                          AuthenticationHelper authenticationHelper, PostService postService, PhoneNumberMapper phoneNumberMapper) {
        this.postService = postService;
        this.phoneNumberMapper = phoneNumberMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/username/{username}")
    public User getByUsername(@PathVariable String username) {
        return userService.getByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/posts")
    public Set<Post> getUserPosts(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return postService.getUserPosts(id);

        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping("/{id}/likedPosts")
    public Set<?> getLikedPosts(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return postService.getUserLikedPosts(loggedInUser, id);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User createUser(@RequestBody UserInDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            return userService.createUser(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    @PutMapping()
    public User updateUser(@RequestHeader HttpHeaders headers, @RequestBody UserInDto user) {
        User loggedInUser = authenticationHelper.tryGetUser(headers);
        User updateParameters = userMapper.fromDto(user);
        return userService.updateUser(updateParameters, loggedInUser.getId());
    }

    @PutMapping("/{id}")
    public User updateUserBlock(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody UserBlockDto userBlockDto) {
        User loggedInUser = authenticationHelper.tryGetUser(headers);
        User updateParameters = userMapper.fromBlockedDto(userBlockDto);

        return userService.updateUserBlockStatus(loggedInUser, updateParameters, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.deleteUserById(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    @PutMapping("/{id}/phonenumber")
    public UserPhoneNumber addPhoneNumber(@RequestHeader HttpHeaders headers, @RequestBody PhoneNumberDto phoneNumberDto, @PathVariable int id) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return userService.addPhoneNumber(id, phoneNumberMapper.fromDto(phoneNumberDto), loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/phonenumber")
    public void deletePhoneNumber(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            userService.deletePhoneNumber(id, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }
}
