package com.example.forumproject.services;

import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.Post;
import com.example.forumproject.repositories.contracts.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository repository;
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.repository = commentRepository;
    }

    @Override
    public Comment getById(int id) {
       return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    @Override
    public List<Comment> getAll() {
        return repository.findAll();
    }

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public void deleteCommentById(int id) {
        repository.deleteById(id);
    }

    @Override
    public Comment createComment(Comment comment) {
        return save(comment);
    }


}
