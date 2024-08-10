package com.example.forumproject.controllers.rest;

import com.example.forumproject.exceptions.*;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.CommentMapper;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.helpers.mapper.TagMapper;
import com.example.forumproject.helpers.mapper.UserMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final TagService tagService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostController(PostService postService, TagService tagService, PostMapper postMapper, CommentMapper commentMapper, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.tagService = tagService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestHeader HttpHeaders headers,
                                         @RequestParam(required = false) Integer minLikes,
                                         @RequestParam(required = false) Integer maxLikes,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String content,
                                         @RequestParam(required = false) String createdBefore,
                                         @RequestParam(required = false) String createdAfter,
                                         @RequestParam(required = false) String postedBy,
                                         @RequestParam(required = false) String tagName,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(required = false) String sortOrder,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
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
                            tagName,
                            sortBy,
                            sortOrder,
                            page,
                            size);
            Pageable pageable = PageRequest.of(page, size);
            Page<?> posts = postService.getAllPosts(user, filterOptions, pageable);
            return ResponseEntity.of(Optional.ofNullable(posts));
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public Object getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (user.getRole().getRoleName().equals("Admin")){
                return post;
            } else{
                return PostMapper.toUserDTO(post);
            }
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("/{id}/likes")
    public Set<?> getLikes(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (post.getLikes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        if (user.getRole().getRoleName().equals("Admin")) {
            return post.getLikes();
        } else {
            return post.getLikes()
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toSet());
        }
    }
    @GetMapping("/{id}/tags")
    public Set<?> getTags(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (post.getTags().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        if (user.getRole().getRoleName().equals("Admin")) {
            return post.getTags();
        } else {
            return post.getTags()
                    .stream()
                    .map(TagMapper::toUserDto)
                    .collect(Collectors.toSet());
        }
    }
    @GetMapping("/{id}/comments")
    public Set<?> getComments(@RequestHeader HttpHeaders headers, @PathVariable int id){
        User user = authenticationHelper.tryGetUser(headers);
        Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (post.getComments().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        if (user.getRole().getRoleName().equals("Admin")) {
            return post.getComments();
        } else {
            return post.getComments()
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toSet());
        }
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
    @PutMapping("/{postId}/tags/{tagId}/addition")
    public void addTagToPost(
            @RequestHeader HttpHeaders headers,
            @PathVariable int postId,
            @PathVariable int tagId){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            tagService.addTagToPost(tagId, postId, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (OperationAlreadyPerformedException e){
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }
    @PutMapping("/{postId}/tags/{tagId}/removal")
    public void removeTagToPost(
            @RequestHeader HttpHeaders headers,
            @PathVariable int postId,
            @PathVariable int tagId){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            tagService.removeTagToPost(tagId, postId, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
