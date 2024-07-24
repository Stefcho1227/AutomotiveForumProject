package com.example.forumproject.helpers;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.CommentInDto;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class CommentMapper {

    private final CommentService commentService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Autowired
    public CommentMapper(CommentService commentService, UserRepository userRepository, PostRepository postRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Comment fromDto(CommentInDto inputData, User user) {
        Comment comment = new Comment();
        comment.setCreatedAt(Timestamp.from(Instant.now()));
        comment.setContent(inputData.getContent());
        comment.setPost(postRepository.findById(inputData.getPostId()).orElseThrow(
                () -> new EntityNotFoundException("Post not found")));
        comment.setCreatedBy(user);

        return comment;
    }





}
