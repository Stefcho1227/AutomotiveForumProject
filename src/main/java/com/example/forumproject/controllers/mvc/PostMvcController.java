package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.helpers.mapper.CommentMapper;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.CommentInDto;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.services.contracts.CommentService;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.TagService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final CommentMapper commentMapper;
    private final TagService tagService;
    @Autowired
    public PostMvcController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper, CommentService commentService, CommentMapper commentMapper, TagService tagService){

        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.tagService = tagService;
    }

    @ModelAttribute("mostLikedPost")
    public Post populateLikedPost() {
        return postService.getMostLikedPost();
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
        return "UserCommentView";
    }
    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable int postId, @ModelAttribute CommentInDto commentDto, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            Post post = postService.getPostById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post", postId));

            Comment comment = commentMapper.fromDto(commentDto, user);
            comment.setPost(post);

            commentService.createComment(comment);

            return "redirect:/posts/" + postId + "/comments";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            return "ErrorView";
        }
    }
    @GetMapping("/new")
    public String showNewPostPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        List<?> tags = tagService.getAllTags(user);
        model.addAttribute("postDto", new PostDto());
        model.addAttribute("tags", tags);
        return "CreatePostView";
    }
    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("postDto") PostDto postDto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "CreatePostView";
        }

        try {
            Set<Tag> tags = tagService.getTagsByIds(postDto.getTagIds());
            Post post = postMapper.fromDto(postDto, user, tags);
            postService.create(post, user);
            return "redirect:/";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("title", "duplicate_post", e.getMessage());
            return "CreatePostView";
        }
    }


}
