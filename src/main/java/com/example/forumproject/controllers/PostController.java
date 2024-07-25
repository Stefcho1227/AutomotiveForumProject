package com.example.forumproject.controllers;

import com.example.forumproject.exceptions.*;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.services.contracts.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

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
    public List<Post> getAllPosts(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) Integer minLikes,
                                  @RequestParam(required = false) Integer maxLikes,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String content,
                                  @RequestParam(required = false) String createdBefore,
                                  @RequestParam(required = false) String createdAfter,
                                  @RequestParam(required = false) String postedBy,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilterOptions filterOptions =
                    new FilterOptions(
                            minLikes,
                            maxLikes,
                            title,
                            content,
                            createdBefore,
                            createdAfter,
                            postedBy,
                            sortBy,
                            sortOrder);
            return postService.getAllPosts(filterOptions);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public Post getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            return postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("/{id}/likes")
    public Set<User> getLikes(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        authenticationHelper.tryGetUser(headers);
        Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return post.getLikes();
    }
    @GetMapping("/{id}/tags")
    public Set<Tag> getTags(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        authenticationHelper.tryGetUser(headers);
        Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return post.getTags();
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
        } catch (BlockedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public Post update(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody PostDto postDto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.fromDto(id, postDto);
            return postService.update(post, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PutMapping("/{id}/likes")
    public void likeAction(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(id).orElseThrow(()->new EntityNotFoundException("Post", id));
            postService.likePost(post, user);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (OperationAlreadyPerformedException e){
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postService.delete(id, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
