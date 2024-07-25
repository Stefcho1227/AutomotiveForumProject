package com.example.forumproject.controllers;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.exceptions.OperationAlreadyPerformedException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.PostMapper;
import com.example.forumproject.helpers.TagMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.dtos.in.TagDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TagController(TagService tagService, TagMapper tagMapper, AuthenticationHelper authenticationHelper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
        this.authenticationHelper = authenticationHelper;
    }
    @GetMapping
    public List<?> getAllTags(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return tagService.getAllTags(user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("{id}")
    public Tag getById(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return tagService.getById(user, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("/{id}/posts")
    public Set<Post> getPosts(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        Tag tag = tagService.getById(user, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return tag.getPosts();
    }
    @PostMapping
    public Tag create(@RequestHeader HttpHeaders headers, @Valid @RequestBody TagDto tagDto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Tag tag = tagMapper.fromDto(tagDto);
            return tagService.create(tag, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public Tag update(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody TagDto tagDto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Tag tag = tagMapper.fromDto(id, tagDto, user);
            return tagService.update(tag, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PutMapping("/{id}/posts/{postId}/adding")
    public void addTagToPost(
            @RequestHeader HttpHeaders headers,
            @PathVariable int id,
            @PathVariable int postId){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            tagService.addTagToPost(id, postId, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (OperationAlreadyPerformedException e){
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }
    @PutMapping("/{id}/posts/{postId}/removing")
    public void removeTagToPost(
            @RequestHeader HttpHeaders headers,
            @PathVariable int id,
            @PathVariable int postId){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            tagService.removeTagToPost(id, postId, user);
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
            tagService.delete(id, user);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
