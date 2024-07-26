package com.example.forumproject;
import com.example.forumproject.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

public class Helpers {

    public static User createMockUser() {
        User mockUser = new User();
        Role mockRole = createMockRole();
        mockUser.setId(1);
        mockUser.setUsername("MockUsername");
        mockUser.setPassword("MockPassword");
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mock@mockemail.com");
        mockUser.setBlocked(false);
        mockUser.setRole(mockRole);
        return mockUser;
    }

    public static Post createMockPost() {
        Post mockPost = new Post();
        mockPost.setId(1);
        mockPost.setTitle("MockTitle");
        mockPost.setContent("MockContent");
        mockPost.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mockPost.setLikesCount(0);
        mockPost.setCreatedBy(createMockUser());
        mockPost.setLikes(new HashSet<>());
        mockPost.setComments(new HashSet<>());
        mockPost.setTags(new HashSet<>());
        return mockPost;
    }

    public static Tag createMockTag() {
        Tag mockTag = new Tag();
        mockTag.setId(1);
        mockTag.setTagName("MockTagName");
        mockTag.setPosts(new HashSet<>());
        return mockTag;
    }
    public static Role createMockRole() {
        Role mockRole = new Role();
        mockRole.setId(1);
        mockRole.setRoleName("User");
        return mockRole;
    }
    public static List<Tag> createMockListOfTags(){
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setId(1);
        tag1.setTagName("MockTag1");
        tag2.setId(2);
        tag2.setTagName("MockTag2");
        return List.of(tag1, tag2);
    }

    public static Comment createMockComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment.setContent("MockContent");
        return comment;
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

