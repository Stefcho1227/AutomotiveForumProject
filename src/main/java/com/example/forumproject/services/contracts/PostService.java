package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
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

    Page<Post> getAllPosts(String title, String tag, int page, int size, String sortBy, String direction);

    Post create(Post post, User user);

    Post update(Post post, User user);

    void delete(int id, User user);

    void likePost(Post post, User user);

    int getPostCount();

    Set<Post> getUserPosts(int id);

    Set<?> getUserLikedPosts(User loggedInUser, int id);

    Post getMostLikedPost();

    List<Post> getTenMostLikedPosts();

    List<Post> getTenMostCommentedPosts();

    List<Post> getTenMostRecentPosts();

    List<Post> getPostsByTag(Tag tag);
}
