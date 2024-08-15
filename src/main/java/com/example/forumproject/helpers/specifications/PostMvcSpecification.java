package com.example.forumproject.helpers.specifications;

import com.example.forumproject.models.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostMvcSpecification {
    public static Specification<Post> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Post> hasTag(String tag) {
        return (root, query, cb) -> cb.like(cb.lower(root.join("tags").get("tagName")), "%" + tag.toLowerCase() + "%");
    }
}
