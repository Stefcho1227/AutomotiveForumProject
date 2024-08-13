package com.example.forumproject.models.dtos.out;

public class TagUserDto {
    private String name;

    public TagUserDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
