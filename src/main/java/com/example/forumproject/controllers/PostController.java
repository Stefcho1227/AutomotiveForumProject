package com.example.forumproject.controllers;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.PostMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.PostDto;
import com.example.forumproject.services.contracts.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostController(PostService postService, PostMapper postMapper, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post get(@PathVariable int id) {
        return postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    public Post create(@RequestHeader HttpHeaders headers, @Valid @RequestBody PostDto postDto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.fromDto(postDto, user);
            return postService.create(post, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    //TODO
    //add functonality to add comments to post
    //with Set<> and relation
}
