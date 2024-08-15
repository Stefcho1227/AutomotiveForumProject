package com.example.forumproject.helpers.mapper;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.CommentDto;
import com.example.forumproject.models.dtos.in.CommentInDto;
import com.example.forumproject.models.dtos.out.CommentOutDto;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                () -> new EntityNotFoundException("Post", inputData.getPostId())));
        comment.setCreatedBy(user);

        return comment;
    }
    public Comment fromDto(int id, CommentDto dto) {
        Comment comment = fromDto(dto);
        comment.setId(id);
        Comment commentRepository = commentService.getById(id);
        comment.setCreatedBy(commentRepository.getCreatedBy());
        comment.setPost(commentRepository.getPost());
        return comment;
    }

    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        return comment;
    }

    public CommentOutDto toDto(Comment comment) {
        CommentOutDto commentOutDto = new CommentOutDto();
        String authorName = comment.getCreatedBy().getFirstName() + " " + comment.getCreatedBy().getLastName();
        commentOutDto.setAuthorName(authorName);
        commentOutDto.setContent(comment.getContent());
        //commentOutDto.setId(comment.getId());
        commentOutDto.setCreatedAt(comment.getCreatedAt());
        return commentOutDto;
    }
    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        return commentDto;
    }

    public List<CommentOutDto> toDtoList(List<Comment> comments) {
        List<CommentOutDto> commentOutDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentOutDtos.add(toDto(comment));
        }
        return commentOutDtos;
    }



}
