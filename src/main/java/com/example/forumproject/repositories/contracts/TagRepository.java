package com.example.forumproject.repositories.contracts;

import com.example.forumproject.models.Comment;
import com.example.forumproject.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}
