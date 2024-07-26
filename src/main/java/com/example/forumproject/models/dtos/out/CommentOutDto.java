package com.example.forumproject.models.dtos.out;

import java.sql.Timestamp;

public class CommentOutDto {
    /*private int id;*/
    private String authorName;
    private String content;
    private Timestamp createdAt;

    public CommentOutDto() {
    }

    public CommentOutDto(String authorName, String content, Timestamp createdAt) {
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
