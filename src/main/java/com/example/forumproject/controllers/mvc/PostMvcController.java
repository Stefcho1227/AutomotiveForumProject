package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.CommentService;
import com.example.forumproject.services.contracts.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final CommentService commentService;
    @Autowired
    public PostMvcController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper, CommentService commentService){

        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentService = commentService;
    }
    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model, HttpSession session) {
        /*User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }*/
        try {
            Post post = postService.getPostById(id).orElseThrow(() -> new EntityNotFoundException("post", id));
            Set<Comment> comments = post.getComments();
            Set<Tag> tags = post.getTags();
            User author = post.getCreatedBy();

            model.addAttribute("postId", id);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("tags", tags);
            model.addAttribute("createdAt", post.getCreatedAt());
            model.addAttribute("likesCount", post.getLikesCount());
            model.addAttribute("author", author);

            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("notFound", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/{postId}/comments")
    public String showPostComments(@PathVariable int postId, Model model) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new EntityNotFoundException("post", postId));
        Set<Comment> comments = post.getComments();
        Set<User> users = comments.stream().map(Comment::getCreatedBy).collect(Collectors.toSet());

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("users", users);
        return "UserView";
    }
}
