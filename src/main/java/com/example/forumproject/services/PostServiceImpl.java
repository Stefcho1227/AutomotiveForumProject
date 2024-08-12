package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.helpers.specifications.PostSpecification;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private static final String POST_ERROR_MESSAGE = "Only post creator can modify a post.";
    private static final String DELETE_POST_ERROR_MESSAGE = "Only post creator or admin or moderator can delete a post.";
    public static final String ADMIN_OR_LOGGER_ERROR = "Should be admin or logged in user to view other's posts likes";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, UserService userService){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public Page<?> getAllPosts(User user, FilterOptions filterOptions, Pageable pageable) {
        Specification<Post> specification = PostSpecification.filterByOption(filterOptions);
        Page<Post> posts = postRepository.findAll(specification, pageable);
        if ("Admin".equals(user.getRole().getRoleName())) {
            return posts;
        } else {
            return posts.map(PostMapper::toUserDTO);
        }
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
        Set<Post> postLikedByUser = user.getPostsLiked();
        if (usersLikedPost.contains(user)) {
            usersLikedPost.remove(user);
            postLikedByUser.remove(post);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            usersLikedPost.add(user);
            postLikedByUser.add(post);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
        userRepository.save(user);
    }

    @Override
    public Set<Post> getUserPosts(int id) {
        User user = userService.getUserById(id).orElseThrow(()->new EntityNotFoundException("User", id));
        return postRepository.findByCreatedBy(user);
    }
    @Override
    public Set<?> getUserLikedPosts(User loggedInUser, int id) {
        validateAccess(loggedInUser, id);
        User user = userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User", id));
        Set<Post> likedPosts = user.getPostsLiked();
        if (loggedInUser.getRole().getRoleName().equals("Admin")){
            return likedPosts;
        }else {
            return likedPosts.stream()
                    .map(PostMapper::toUserDTO).collect(Collectors.toSet());
        }
    }

    @Override
    public Post getMostLikedPost() {
        return postRepository.findAll().stream()
                .max(Comparator.comparingInt(post -> post.getLikes().size()))
                .orElse(null);
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
    private void validateAccess(User loggedUser, int id){
        if (!loggedUser.getRole().getRoleName().equals("Admin") && loggedUser.getId() != id) {
            throw new AuthorizationException(ADMIN_OR_LOGGER_ERROR);
        }
    }
}
