package com.example.forumproject.models.dtos.out;

public class TagUserDto {
    private String tagName;

    public TagUserDto(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
