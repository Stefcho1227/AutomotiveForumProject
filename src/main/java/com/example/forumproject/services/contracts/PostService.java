package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Post;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> getPostById(int id);
    List<Post> getAllPosts();
}
