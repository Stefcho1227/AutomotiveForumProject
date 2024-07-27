package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;

import java.util.List;

public interface CommentService {
    Comment getById(int id);

    List<Comment> getAll(FilterOptions filterOptions);

    Comment save(Comment comment);

    void deleteCommentById(int id, User loggedInUser);

    Comment createComment(Comment comment);

    Comment updateComment(Comment comment, User loggedInUser, int commentId);
}
