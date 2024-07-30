package com.example.forumproject.helpers.mapper;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.dtos.out.*;
import com.example.forumproject.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    private final PostService postService;
    @Autowired
    public PostMapper(PostService postService) {
        this.postService = postService;
    }
    public Post fromDto(int id, PostDto postDto){
        Post repositoryPost = postService.getPostById(id).orElseThrow(() -> new EntityNotFoundException("Post", id));
        repositoryPost.setTitle(postDto.getTitle());
        repositoryPost.setContent(postDto.getContent());
        return repositoryPost;
    }
    public Post fromDto(PostDto postDto, User user){
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedBy(user);
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        post.setCreatedAt(timestamp);

        return post;
    }
    public static PostUserDto toUserDTO(Post post) {
        PostUserDto dto = new PostUserDto();
        dto.setCreatedBy(new UserOutDto(
                post.getCreatedBy().getFirstName(),
                post.getCreatedBy().getLastName(),
                post.getCreatedBy().getUsername()));
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikesCount(post.getLikesCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setComments(post.getComments()
                .stream()
                .map(comment -> new CommentOutDto(
                        comment.getCreatedBy().getUsername(),
                        comment.getContent(),
                        comment.getCreatedAt()))
                .collect(Collectors.toSet()));
        dto.setLikes(post.getLikes()
                .stream()
                .map(user -> new UserOutDto(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUsername()
                )).collect(Collectors.toSet()));
        dto.setTags(post.getTags()
                .stream().map(tag -> new TagUserDto(tag.getTagName())).collect(Collectors.toSet()));
        return dto;
    }
}
