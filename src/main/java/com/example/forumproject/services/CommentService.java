package com.example.forumproject.services;

import com.example.forumproject.models.Comment;

import java.util.List;

public interface CommentService {
    Comment getById(int id);

    List<Comment> getAll();

    Comment save(Comment comment);

    void deleteCommentById(int id);
}
