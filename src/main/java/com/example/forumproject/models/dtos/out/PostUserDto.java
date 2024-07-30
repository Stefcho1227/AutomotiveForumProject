package com.example.forumproject.models.dtos.out;

import com.example.forumproject.models.Comment;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;

import java.sql.Timestamp;
import java.util.Set;

public class PostUserDto {
    private UserOutDto createdBy;
    private String title;
    private String content;
    private int likesCount;
    private Timestamp createdAt;
    private Set<CommentOutDto> comments;
    private Set<TagUserDto> tags;
    private Set<UserOutDto> likes;

    public PostUserDto() {
    }

    public Set<UserOutDto> getLikes() {
        return likes;
    }

    public void setLikes(Set<UserOutDto> likes) {
        this.likes = likes;
    }

    public UserOutDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserOutDto createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Set<CommentOutDto> getComments() {
        return comments;
    }

    public void setComments(Set<CommentOutDto> comments) {
        this.comments = comments;
    }

    public Set<TagUserDto> getTags() {
        return tags;
    }

    public void setTags(Set<TagUserDto> tags) {
        this.tags = tags;
    }
}
