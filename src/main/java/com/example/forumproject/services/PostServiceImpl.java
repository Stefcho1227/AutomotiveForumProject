package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.specifications.PostSpecification;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private static final String POST_ERROR_MESSAGE = "Only post creator can modify a post.";
    private static final String DELETE_POST_ERROR_MESSAGE = "Only post creator or admin or moderator can delete a post.";
    private static final String MORE_THAN_ONCE_LIKED_ERROR = "The post should be liked only once";
    private static final String MORE_THAN_ONCE_REMOVE_LIKE_ERROR = "The like from post can be removed only once";
    private final PostRepository postRepository;
    private final UserService userService;
    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserService userService){
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getAllPosts(FilterOptions filterOptions) {
        Specification<Post> specification = PostSpecification.filterByOption(filterOptions);
        return postRepository.findAll(specification);
    }

    @Override
    public Post create(Post post, User user) {
        AuthenticationHelper.checkUserBlockStatus(user);
        return postRepository.save(post);
    }

    @Override
    public Post update(Post post, User user) {

        AuthenticationHelper.checkUserBlockStatus(user);
        checkModifyPermissions(post.getId(), user);
        return postRepository.save(post);
    }

    @Override
    public void delete(int id, User user) {
        AuthenticationHelper.checkUserBlockStatus(user);
        checkModifyPermissionsToDelete(id, user);
        Post postToDelete = postRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Post", id));
        postRepository.delete(postToDelete);
    }

    @Override
    public void likePost(Post post, User user) {
        AuthenticationHelper.checkUserBlockStatus(user);
        Set<User> usersLikedPost = post.getLikes();
        if (usersLikedPost.contains(user)){
            usersLikedPost.remove(user);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            usersLikedPost.add(user);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
    }

    @Override
    public Set<Post> getUserPosts(int id) {
        User user = userService.getUserById(id).orElseThrow(()->new EntityNotFoundException("User", id));
        return postRepository.findByCreatedBy(user);
    }
    private void checkModifyPermissions(int postId, User user) {
        Post repositoryPost = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("Post", postId));
        if (!repositoryPost.getCreatedBy().equals(user)) {
            throw new AuthorizationException(POST_ERROR_MESSAGE);
        }
    }
    private void checkModifyPermissionsToDelete(int postId, User user) {
        Post repositoryPost = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("Post", postId));
        if (repositoryPost.getCreatedBy().equals(user)) {
            return;
        }
        String roleName = user.getRole().getRoleName();
        if (roleName.equals("Admin") || roleName.equals("Moderator")){
            return;
        }
        throw new AuthorizationException(DELETE_POST_ERROR_MESSAGE);
    }
}
