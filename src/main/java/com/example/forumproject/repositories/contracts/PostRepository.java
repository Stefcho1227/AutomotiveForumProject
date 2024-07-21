package com.example.forumproject.repositories.contracts;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
