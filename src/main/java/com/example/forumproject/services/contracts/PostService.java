package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostService {
    Optional<Post> getPostById(int id);
    List<Post> getAllPosts(FilterOptions filterOptions);
    Post create(Post post, User user);

    Post update(Post post, User user);

    void delete(int id, User user);

    void likePost(Post post, User user);

    Set<Post> getUserPosts(int id);
}
