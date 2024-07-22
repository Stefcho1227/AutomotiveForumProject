package com.example.forumproject.services;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private static final String MODIFY_BEER_ERROR_MESSAGE = "Only admin or post creator can modify a beer.";

    private final PostRepository postRepository;
    @Autowired
    public PostServiceImpl(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @Override
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post create(Post post, User user) {
        return postRepository.save(post);
    }

    //TODO
    //method to check if the user is user or admin/moderator for user to edit which HE CREATED and post which HE CREATED
    //and for admin/moderator to delete posts ANY POST
}
