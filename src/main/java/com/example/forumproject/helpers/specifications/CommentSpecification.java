package com.example.forumproject.helpers.specifications;

import com.example.forumproject.models.Comment;
import com.example.forumproject.models.options.FilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentSpecification {
    public static Specification<Comment> filterByOption(FilterOptions filterOptions) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


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
