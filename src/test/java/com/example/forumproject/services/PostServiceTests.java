package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
//TODO add method to check getAll filtering
@ExtendWith(MockitoExtension.class)
public class PostServiceTests {
    @Mock
    PostRepository mockPostRepository;
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
        Assertions.assertEquals(1, result.get().getId());
    }


    @Test
    public void create_Should_SavePost_When_Valid() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        Post result = postService.create(mockPost, mockUser);
        Assertions.assertNotNull(result);
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
        Assertions.assertNotNull(postToUpdate);
        Assertions.assertEquals("NewPostName", postToUpdate.getTitle());
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
        Set<User> likes = mockPost.getLikes();
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        postService.likePost(mockPost, mockUser);
        Assertions.assertTrue(likes.contains(mockUser));
    }

    @Test
    public void likePost_Should_RemoveLike_When_AlreadyLiked() {
        User mockUser = Helpers.createMockUser();
        Post mockPost = Helpers.createMockPost();
        mockPost.getLikes().add(mockUser);
        Set<User> likes = mockPost.getLikes();
        Mockito.when(mockPostRepository.save(mockPost)).thenReturn(mockPost);
        postService.likePost(mockPost, mockUser);
        Assertions.assertFalse(likes.contains(mockUser));
    }

    @Test
    public void getUserPosts_Should_ReturnPosts_When_UserExists() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserService.getUserById(1)).thenReturn(Optional.of(mockUser));
        Mockito.when(mockPostRepository.findByCreatedBy(mockUser)).thenReturn(Set.of(new Post()));
        Set<Post> result = postService.getUserPosts(1);
        Assertions.assertFalse(result.isEmpty());
    }
}
