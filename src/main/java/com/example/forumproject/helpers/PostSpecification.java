package com.example.forumproject.helpers;

import com.example.forumproject.models.Post;
import com.example.forumproject.models.options.FilterPostOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
    public static Specification<Post> filterByOption(FilterPostOptions filterPostOptions) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterPostOptions.getMinLikes().ifPresent(minLikes ->
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("likesCount"), minLikes))
            );
            filterPostOptions.getMaxLikes().ifPresent(maxLikes ->
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("likesCount"), maxLikes))
            );
            filterPostOptions.getTitle().ifPresent(title ->
                predicates.add(
                        criteriaBuilder.like(root.get("title"), "%" + title + "%"))
            );

            filterPostOptions.getContent().ifPresent(content ->
                predicates.add(
                        criteriaBuilder.like(root.get("content"), "%" + content + "%"))
            );

            filterPostOptions.getCreatedBefore().ifPresent(createdBefore -> {
                LocalDateTime dateBefore = LocalDateTime.parse(createdBefore, DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dateBefore));
            });

            filterPostOptions.getCreatedAfter().ifPresent(createdAfter -> {
                LocalDateTime dateAfter = LocalDateTime.parse(createdAfter, DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateAfter));
            });

            filterPostOptions.getPostedBy().ifPresent(postedBy ->
                predicates.add(
                        criteriaBuilder.equal(root.get("createdBy").get("username"), postedBy))
            );
            query.where(predicates.toArray(new Predicate[0]));

            if (filterPostOptions.getSortBy().isPresent() && filterPostOptions.getSortOrder().isPresent()) {
                String sortBy = filterPostOptions.getSortBy().get();
                String sortOrder = filterPostOptions.getSortOrder().get();
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
