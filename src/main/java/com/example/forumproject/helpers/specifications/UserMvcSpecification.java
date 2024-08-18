package com.example.forumproject.helpers.specifications;

import com.example.forumproject.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserMvcSpecification {
    public static Specification<User> hasFirstName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }
}
