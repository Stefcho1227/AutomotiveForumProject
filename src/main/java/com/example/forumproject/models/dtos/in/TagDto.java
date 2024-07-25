package com.example.forumproject.models.dtos.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TagDto {
    @NotNull(message = "Title can't be empty")
    @Size(max = 64, message = "Title should be max 64 symbols")
    private String tagName;

    public TagDto() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
