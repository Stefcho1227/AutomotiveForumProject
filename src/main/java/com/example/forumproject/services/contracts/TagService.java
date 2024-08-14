package com.example.forumproject.services.contracts;

import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {
    List<Tag> getAllTags(User user);

    Tag create(Tag tag, User user);

    Optional<Tag> getById(User user, int tagId);

    Optional<Tag> getById(int tagId);

    void addTagToPost(int id, int postId, User user);

    void removeTagToPost(int id, int postId, User user);

    Tag update(Tag tag, User user);

    void delete(int id, User user);
    Set<Tag> getTagsByIds(Set<Integer> tagIds);
}
