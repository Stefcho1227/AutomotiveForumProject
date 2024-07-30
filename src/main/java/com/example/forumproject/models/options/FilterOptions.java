package com.example.forumproject.models.options;

import java.util.Optional;

public class FilterOptions {

    private Optional<Integer> minLikes;
    private Optional<Integer> maxLikes;
    private Optional<String> title;
    private Optional<String> content;
    private Optional<String> createdBefore;
    private Optional<String> createdAfter;
    private Optional<String> postedBy;
    private Optional<String> tagName;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterOptions(Integer minLikes,
                         Integer maxLikes,
                         String title,
                         String content,
                         String createdBefore,
                         String createdAfter,
                         String postedBy,
                         String tagName,
                         String sortBy,
                         String sortOrder) {
        this.minLikes = Optional.ofNullable(minLikes);
        this.maxLikes = Optional.ofNullable(maxLikes);
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.createdBefore = Optional.ofNullable(createdBefore);
        this.createdAfter = Optional.ofNullable(createdAfter);
        this.postedBy = Optional.ofNullable(postedBy);
        this.tagName = Optional.ofNullable(tagName);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public FilterOptions(String content,
                         String createdBefore,
                         String createdAfter,
                         String postedBy,
                         String sortBy,
                         String sortOrder) {
        this.content = Optional.ofNullable(content);
        this.createdBefore = Optional.ofNullable(createdBefore);
        this.createdAfter = Optional.ofNullable(createdAfter);
        this.postedBy = Optional.ofNullable(postedBy);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<Integer> getMinLikes() {
        return minLikes;
    }

    public void setMinLikes(Optional<Integer> minLikes) {
        this.minLikes = minLikes;
    }

    public Optional<Integer> getMaxLikes() {
        return maxLikes;
    }

    public void setMaxLikes(Optional<Integer> maxLikes) {
        this.maxLikes = maxLikes;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public void setTitle(Optional<String> title) {
        this.title = title;
    }

    public Optional<String> getContent() {
        return content;
    }

    public void setContent(Optional<String> content) {
        this.content = content;
    }

    public Optional<String> getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Optional<String> createdBefore) {
        this.createdBefore = createdBefore;
    }

    public Optional<String> getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Optional<String> createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Optional<String> getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Optional<String> postedBy) {
        this.postedBy = postedBy;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Optional<String> getTagName() {
        return tagName;
    }

    public void setTagName(Optional<String> tagName) {
        this.tagName = tagName;
    }
}
