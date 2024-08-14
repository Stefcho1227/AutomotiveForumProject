package com.example.forumproject.controllers.mvc;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.services.contracts.PostService;
import com.example.forumproject.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tag")
public class TagMvcController {
    private final TagService tagService;
    private final PostService postService;

    @Autowired
    public TagMvcController(TagService tagService, PostService postService) {
        this.tagService = tagService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public String showTagView(@PathVariable int id, Model model) {

        try {
            Tag tag = tagService.getById(id).orElseThrow(() -> new EntityNotFoundException("Tag"));
            model.addAttribute("tag", tag);
            FilterOptions filterOptions = new FilterOptions(tag.getTagName());
            List<Post> postsWithTag = postService.getPostsByTag(tag);
            model.addAttribute("postsWithTag", postsWithTag);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("notFound", e.getMessage());
            return "ErrorView";
        }
        return "TagView";
    }
}
