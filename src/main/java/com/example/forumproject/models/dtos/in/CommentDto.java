package com.example.forumproject.models.dtos.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CommentDto {

    @NotEmpty(message = "Content can't be empty!")
    @Size(max = 8192, message = "The content cannot exceed 8192 characters!")
    private String content;

    public CommentDto() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
