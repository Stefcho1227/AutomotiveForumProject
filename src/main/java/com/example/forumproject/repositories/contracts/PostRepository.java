package com.example.forumproject.repositories.contracts;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
    Set<Post> findByCreatedBy(User user);
}
