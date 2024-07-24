package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.repositories.contracts.CommentRepository;
import com.example.forumproject.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
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

    public Comment updateComment(Comment inputData, User loggedInUser, int commentId) {
        checkCommentUpdatePermission(commentId, loggedInUser);
        Comment commentToUpdate = getById(commentId);
        if (inputData.getContent() != null) {
            commentToUpdate.setContent(inputData.getContent());
        }

        return save(commentToUpdate);
    }

    public void checkCommentUpdatePermission(int commentId, User loggedInUser) {
        Comment commentToCheck = getById(commentId);
        if (commentToCheck.getCreatedBy().equals(loggedInUser)) {
            return;
        } else {
            throw new AuthorizationException("You do not have permission to update this comment");
        }
    }
}
