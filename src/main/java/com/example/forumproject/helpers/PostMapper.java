package com.example.forumproject.helpers;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.PostDto;
import com.example.forumproject.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class PostMapper {
    private final PostService postService;
    @Autowired
    public PostMapper(PostService postService) {
        this.postService = postService;
    }
    //TODO add createdAt too
    public Post fromDto(int id, PostDto postDto, User user){
        Post repositoryPost = postService.getPostById(id).orElseThrow(() -> new EntityNotFoundException("Post", id));
        repositoryPost.setTitle(postDto.getTitle());
        repositoryPost.setContent(postDto.getContent());
        repositoryPost.setCreatedBy(user);
        return repositoryPost;
    }
    public Post fromDto(PostDto postDto, User user){
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedBy(user);
        return post;
    }
}
