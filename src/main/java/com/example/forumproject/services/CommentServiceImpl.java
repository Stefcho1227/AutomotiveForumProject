package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.specifications.CommentSpecification;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.CommentRepository;
import com.example.forumproject.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    public static final String ADMIN_ROLE_NAME = "Admin";
    private CommentRepository repository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.repository = commentRepository;
    }

    @Override
    public Comment getById(int id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment"));
    }

    @Override
    public List<Comment> getAll(FilterOptions filterOptions) {
        Specification<Comment> specification = CommentSpecification.filterByOption(filterOptions);
        List<Comment> comments = repository.findAll(specification);
        if (comments.isEmpty()) {
            throw new EntityNotFoundException("Comments", "such", "criteria");
        } else {
            return comments;
        }
    }

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public void deleteCommentById(int id, User loggedInUser) {
        throwIfNoPermissionToDelete(id, loggedInUser);
        repository.deleteById(id);
    }

    @Override
    public Comment createComment(Comment comment) {
        return save(comment);
    }

    public Comment updateComment(Comment inputData, User loggedInUser, int commentId) {
        throwIfNoPermissionToUpdate(commentId, loggedInUser);
        Comment commentToUpdate = getById(commentId);
        if (inputData.getContent() != null) {
            commentToUpdate.setContent(inputData.getContent());
        }

        return save(commentToUpdate);
    }

    private void throwIfNoPermissionToUpdate(int commentId, User loggedInUser) {
        Comment commentToCheck = getById(commentId);
        if (!commentToCheck.getCreatedBy().equals(loggedInUser)) {
            throw new AuthorizationException("You do not have permission to update this comment");
        }
    }

    private void throwIfNoPermissionToDelete(int commentId, User loggedInUser) {
        Comment commentToCheck = getById(commentId);
        if (!commentToCheck.getCreatedBy().equals(loggedInUser) || loggedInUser.getRole().getRoleName().equals(ADMIN_ROLE_NAME)) {
            throw new AuthorizationException("You do not have permission to delete this comment");
        }
    }
}
