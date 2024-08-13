package com.example.forumproject.repositories.contracts;

import com.example.forumproject.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Set<Tag> findByIdIn(Set<Integer> ids);
}
