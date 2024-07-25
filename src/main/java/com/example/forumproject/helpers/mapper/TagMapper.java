package com.example.forumproject.helpers.mapper;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.dtos.in.TagDto;
import com.example.forumproject.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TagMapper {
    private final TagService tagService;
    @Autowired
    public TagMapper(TagService tagService){
        this.tagService = tagService;
    }
    public Tag fromDto(TagDto tagDto){
        Tag tag = new Tag();
        tag.setTagName(tagDto.getTagName());
        return tag;
    }
    public Tag fromDto(int id, TagDto tagDto, User user){
        Tag repositoryTag = tagService.getById(user, id).orElseThrow(()->new EntityNotFoundException("Tag", id));
        repositoryTag.setTagName(tagDto.getTagName());
        return repositoryTag;
    }
}
