package com.example.forumproject.controllers;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.CommentMapper;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.CommentInDto;
import com.example.forumproject.models.dtos.out.CommentOutDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentController(CommentService commentService, CommentMapper commentMapper, AuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<CommentOutDto> getComments(@RequestHeader HttpHeaders headers,
                                           @RequestParam(required = false) String content,
                                           @RequestParam(required = false) String createdBefore,
                                           @RequestParam(required = false) String createdAfter,
                                           @RequestParam(required = false) String createdBy,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortOrder) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilterOptions filterOptions =
                    new FilterOptions(
                            content,
                            createdBefore,
                            createdAfter,
                            createdBy,
                            sortBy,
                            sortOrder);
            return commentMapper.toDtoList(commentService.getAll(filterOptions));
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public CommentOutDto getById(@PathVariable int id) {
        try {
            return commentMapper.toDto(commentService.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PostMapping
    public CommentOutDto addComment(@RequestHeader HttpHeaders headers, @RequestBody CommentInDto commentInDto) {
        User loggedInUser = null;
        try {
            loggedInUser = authenticationHelper.tryGetUser(headers);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        Comment comment = commentMapper.fromDto(commentInDto, loggedInUser);

        return commentMapper.toDto(commentService.createComment(comment));
    }

    @PutMapping("/{id}")
    public CommentOutDto updateComment(@RequestHeader HttpHeaders headers, @RequestBody CommentInDto commentInDto, @PathVariable int id) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            Comment inputData = commentMapper.fromDto(commentInDto, loggedInUser);
            return commentMapper.toDto(commentService.updateComment(inputData, loggedInUser, id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User loggedInUser = authenticationHelper.tryGetUser(headers);
        try {
            commentService.deleteCommentById(id, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
