package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostService {
    Optional<Post> getPostById(int id);
    Page<?> getAllPosts(User user, FilterOptions filterOptions, Pageable pageable);
    Post create(Post post, User user);

    Post update(Post post, User user);

    void delete(int id, User user);

    void likePost(Post post, User user);

    Set<Post> getUserPosts(int id);
    Set<?> getUserLikedPosts(User loggedInUser, int id);
    Post getMostLikedPost();
}
