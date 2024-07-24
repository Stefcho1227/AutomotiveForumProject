package com.example.forumproject.controllers;

import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.CommentMapper;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.CommentInDto;
import com.example.forumproject.models.dtos.out.CommentOutDto;
import com.example.forumproject.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    //TODO improve the data being sent
    @GetMapping
    public List<CommentOutDto> getComments() {
        List<Comment> comments = commentService.getAll();
        List<CommentOutDto> commentOutDtos = commentMapper.toDtoList(comments);
        return commentOutDtos;
    }

    @GetMapping("/{id}")
    public CommentOutDto getById(@PathVariable int id) {
        return commentMapper.toDto(commentService.getById(id));
    }

    @PostMapping
    public CommentOutDto addComment(@RequestHeader HttpHeaders headers, @RequestBody CommentInDto commentInDto) {
        User loggedInUser = authenticationHelper.tryGetUser(headers);

        Comment comment = commentMapper.fromDto(commentInDto, loggedInUser);

        return commentMapper.toDto(commentService.createComment(comment));
    }









}
