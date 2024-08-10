package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.mapper.PostMapper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.PostDto;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
//TODO add method to check getAll filtering
@ExtendWith(MockitoExtension.class)
public class PostServiceTests {
    @Mock
    PostRepository mockPostRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    UserService mockUserService;
    @InjectMocks
    PostServiceImpl postService;
    @Test
    public void getPostById_Should_ReturnPost_When_MatchExists() {
        Post mockPost = Helpers.createMockPost();
        Mockito.when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        Optional<Post> result = postService.getPostById(1);
        Assertions.assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }


    @Test
    public void create_Should_SavePost_When_Valid() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        Post result = postService.create(mockPost, mockUser);
        assertNotNull(result);
    }
    @Test
    public void update_Should_UpdatePost_When_Valid() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        mockPost.setCreatedBy(mockUser);
        when(mockPostRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        mockPost.setTitle("NewPostName");
        Post postToUpdate = postService.update(mockPost, mockUser);
        assertNotNull(postToUpdate);
        assertEquals("NewPostName", postToUpdate.getTitle());
        verify(mockPostRepository, times(1)).save(mockPost);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreator() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        User creator = Helpers.createMockUser();
        creator.setId(2);
        mockPost.setCreatedBy(creator);
        Mockito.when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        Assertions.assertThrows(AuthorizationException.class, () -> postService.update(mockPost, mockUser));
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotCreatorOrAdminOrModerator() {
        User mockUser = Helpers.createMockUser();
        mockUser.getRole().setRoleName("User");
        User otherUser = Helpers.createMockUser();
        otherUser.setId(2);
        otherUser.getRole().setRoleName("User");
        Post mockPost = Helpers.createMockPost();
        User creator = Helpers.createMockUser();
        mockPost.setCreatedBy(creator);
        Mockito.when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        Assertions.assertThrows(AuthorizationException.class, () -> postService.delete(1, otherUser));
    }

    @Test
    public void delete_Should_DeletePost_When_UserIsAdmin() {
        User mockUser = Helpers.createMockUser();
        mockUser.getRole().setRoleName("Admin");
        Post mockPost = Helpers.createMockPost();
        User creator = Helpers.createMockUser();
        creator.setId(2);
        creator.getRole().setRoleName("User");
        mockPost.setCreatedBy(creator);
        Mockito.when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        postService.delete(1, mockUser);
        Mockito.verify(mockPostRepository, Mockito.times(1)).delete(mockPost);
    }

    @Test
    public void likePost_Should_AddLike_When_NotLiked() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        mockPost.getLikes().clear();
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        postService.likePost(mockPost, mockUser);
        Assertions.assertTrue(mockPost.getLikes().contains(mockUser));
        Mockito.verify(mockPostRepository).save(mockPost);
        Mockito.verify(mockUserRepository).save(mockUser);
    }

    @Test
    public void likePost_Should_RemoveLike_When_AlreadyLiked() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        mockPost.getLikes().add(mockUser);
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        postService.likePost(mockPost, mockUser);
        Assertions.assertFalse(mockPost.getLikes().contains(mockUser));
        Mockito.verify(mockPostRepository).save(mockPost);
        Mockito.verify(mockUserRepository).save(mockUser);
    }

    @Test
    public void getUserPosts_Should_ReturnPosts_When_UserExists() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserService.getUserById(1)).thenReturn(Optional.of(mockUser));
        Mockito.when(mockPostRepository.findByCreatedBy(mockUser)).thenReturn(Set.of(new Post()));
        Set<Post> result = postService.getUserPosts(1);
        Assertions.assertFalse(result.isEmpty());
    }
    @Test
    public void getUserLikedPosts_Should_ReturnAllLikedPosts_When_UserIsAdmin() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");

        User targetUser = Helpers.createMockUser();
        targetUser.setId(2);
        Post post1 = Helpers.createMockPost();
        Post post2 = Helpers.createMockPost();
        post2.setId(2);
        targetUser.getPostsLiked().add(post1);
        targetUser.getPostsLiked().add(post2);

        when(mockUserRepository.findById(targetUser.getId())).thenReturn(Optional.of(targetUser));

        Set<?> result = postService.getUserLikedPosts(adminUser, targetUser.getId());

        assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(post1));
        Assertions.assertTrue(result.contains(post2));
    }

    @Test
    public void getUserLikedPosts_Should_ReturnMappedPosts_When_UserIsNotAdmin() {
        User regularUser = Helpers.createMockUser();

        regularUser.setId(2);

        Post post1 = Helpers.createMockPost();
        Post post2 = Helpers.createMockPost();
        post2.setId(2);
        regularUser.getPostsLiked().add(post1);
        regularUser.getPostsLiked().add(post2);

        when(mockUserRepository.findById(regularUser.getId())).thenReturn(Optional.of(regularUser));

        Set<?> result = postService.getUserLikedPosts(regularUser, regularUser.getId());

        assertEquals(2, result.size());
    }
    @Test
    public void getAllPosts_Should_ReturnRawPosts_When_UserIsAdmin() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");

        Post post1 = Helpers.createMockPost();
        Post post2 = Helpers.createMockPost();
        Page<Post> postPage = new PageImpl<>(List.of(post1, post2));

        when(mockPostRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(postPage);

        Page<?> result = postService.getAllPosts(adminUser, new FilterOptions(null, null, null, null, null, null, null, null, null, null, null, null), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(postPage.getTotalElements(), result.getTotalElements());
        assertEquals(postPage.getContent(), result.getContent());
    }
}
