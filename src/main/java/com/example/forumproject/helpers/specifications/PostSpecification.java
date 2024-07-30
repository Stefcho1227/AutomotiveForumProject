package com.example.forumproject.helpers.specifications;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.options.FilterOptions;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostSpecification {
    public static Specification<Post> filterByOption(FilterOptions filterOptions) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getMinLikes().ifPresent(minLikes ->
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("likesCount"), minLikes))
            );
            filterOptions.getMaxLikes().ifPresent(maxLikes ->
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("likesCount"), maxLikes))
            );
            filterOptions.getTitle().ifPresent(title ->
                predicates.add(
                        criteriaBuilder.like(root.get("title"), "%" + title + "%"))
            );

            filterOptions.getContent().ifPresent(content ->
                predicates.add(
                        criteriaBuilder.like(root.get("content"), "%" + content + "%"))
            );

            filterOptions.getCreatedBefore().ifPresent(createdBefore -> {
                LocalDateTime dateBefore = LocalDateTime.parse(createdBefore, DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dateBefore));
            });

            filterOptions.getCreatedAfter().ifPresent(createdAfter -> {
                LocalDateTime dateAfter = LocalDateTime.parse(createdAfter, DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateAfter));
            });
            filterOptions.getTagName().ifPresent(tagName->{
                Join< Object, Object> tags = root.join("tags");
                predicates.add(
                        criteriaBuilder.equal(tags.get("tagName"), tagName));
                    });

            filterOptions.getPostedBy().ifPresent(postedBy ->
                predicates.add(
                        criteriaBuilder.equal(root.get("createdBy").get("username"), postedBy))
            );
            query.where(predicates.toArray(new Predicate[0]));

            if (filterOptions.getSortBy().isPresent() && filterOptions.getSortOrder().isPresent()) {
                String sortBy = filterOptions.getSortBy().get();
                String sortOrder = filterOptions.getSortOrder().get();
                if (sortOrder.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
                }
            }

            return query.getRestriction();
        };
    }
}
