package com.example.forumproject.helpers;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
//TODO there is some discrepency with the timezones when you send a get request.
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        post.setCreatedAt(timestamp);

        return post;
    }
}
