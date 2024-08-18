package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.AuthenticationFailureException;
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
import com.example.forumproject.models.dtos.in.CommentDto;
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

import java.util.Comparator;
import java.util.LinkedHashSet;
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
    public PostMvcController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper, CommentService commentService, CommentMapper commentMapper, TagService tagService) {

        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            Post post = postService.getPostById(id).orElseThrow(() -> new EntityNotFoundException("post", id));
            Set<Comment> comments = post.getComments();
            Set<Tag> tags = post.getTags();
            User author = post.getCreatedBy();
            boolean isAuthor = author.getId() == user.getId();

            model.addAttribute("postId", id);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("tags", tags);
            model.addAttribute("createdAt", post.getCreatedAt());
            model.addAttribute("likesCount", post.getLikesCount());
            model.addAttribute("author", author);
            model.addAttribute("isAuthor", isAuthor);

            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("notFound", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{postId}/comments")
    public String showPostComments(@PathVariable int postId, Model model, HttpSession session) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new EntityNotFoundException("post", postId));
        Set<Comment> comments = post.getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt)).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<User> users = comments.stream().map(Comment::getCreatedBy).collect(Collectors.toSet());
        User currentUser = authenticationHelper.tryGetCurrentUser(session);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "UserCommentView";
    }

    @GetMapping("/{postId}/like")
    public String addLikeToPost(@PathVariable int postId, @RequestParam(required = false) String redirectUrl, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            Post post = postService.getPostById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post", postId));

            postService.likePost(post, user);

            return "redirect:" + (redirectUrl != null ? redirectUrl : "/posts/" + postId);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            return "ErrorView";
        }
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
        List<Tag> tags = tagService.getAllTags(user);
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

    @GetMapping("/{postId}/edit")
    public String showEditPostPage(@PathVariable int postId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        List<Tag> tags = tagService.getAllTags(user);
        Post post = postService.getPostById(postId).orElseThrow();
        PostDto postDto = postMapper.toDto(post);

        model.addAttribute("postDto", postDto);
        model.addAttribute("tags", tags);
        model.addAttribute("postId", postId);
        return "EditPostView";
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@Valid @ModelAttribute("postDto") PostDto dto, HttpSession session, BindingResult bindingResult, Model model, @PathVariable int postId) {

        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        Post post = postService.getPostById(postId).orElseThrow();

        if (post.getCreatedBy().getId() != user.getId()) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("unauthorized", "You are not authorized to edit this post.");
            return "ErrorView";
        }

        try {
            post = postMapper.fromDto(postId, dto);
            postService.update(post, user);
            return "redirect:/profile";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("unauthorized", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{postId}/comments/{commentId}/edit")
    public String showEditCommentPage(@PathVariable int postId, @PathVariable int commentId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            Comment comment = commentService.getById(commentId);
            CommentDto commentDto = commentMapper.toCommentDto(comment);
            model.addAttribute("postId", postId);
            model.addAttribute("commentDto", commentDto);
            model.addAttribute("commentId", commentId);
            return "EditCommentView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{postId}/comments/{commentId}/edit")
    public String editComment(@Valid @ModelAttribute("commentDto") CommentDto dto, HttpSession session,
                              BindingResult bindingResult, Model model,
                              @PathVariable int postId, @PathVariable int commentId) {

        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            return "EditCommentView";
        }

        try {
            Comment comment = commentMapper.fromDto(commentId, dto);
            commentService.updateComment(comment, user, commentId);
            model.addAttribute("postId", postId);
            return "redirect:/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("unauthorized", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{postId}/delete")
    public String deletePost(@PathVariable int postId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            postService.delete(postId, user);
            return "redirect:/";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{postId}/comments/{commentId}/delete")
    public String deleteCommentOfPost(@PathVariable int postId, @PathVariable int commentId,
                                      Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            Post post = postService.getPostById(postId).orElseThrow();
            commentService.deleteCommentById(commentId, user);
            model.addAttribute("post", post);
            return "redirect:/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}
