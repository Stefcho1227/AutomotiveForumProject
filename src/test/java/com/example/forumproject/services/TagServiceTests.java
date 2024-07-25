package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.OperationAlreadyPerformedException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.out.TagAdminDto;
import com.example.forumproject.models.dtos.out.TagUserDto;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.TagRepository;
import com.example.forumproject.services.contracts.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {
    @Mock
    private TagRepository mockTagRepository;

    @Mock
    private PostRepository mockPostRepository;

    @InjectMocks
    private TagServiceImpl tagService;
    @Test
    public void getAllTags_Should_ReturnTagForAdmin(){
        User userAdmin = Helpers.createMockUser();
        userAdmin.getRole().setRoleName("Admin");
        List<Tag> tags = Helpers.createMockListOfTags();
        when(mockTagRepository.findAll()).thenReturn(tags);
        List<?> result = tagService.getAllTags(userAdmin);
        Assertions.assertEquals(2, result.size());
        TagAdminDto dto = (TagAdminDto) result.get(0);
        Assertions.assertEquals(1, dto.getTagId());
        Assertions.assertEquals("MockTag1", dto.getTagName());
    }
    @Test
    public void getAllTags_Should_ReturnTagForUser(){
        User userNotAdmin = Helpers.createMockUser();
        userNotAdmin.getRole().setRoleName("User");
        List<Tag> tags = Helpers.createMockListOfTags();
        when(mockTagRepository.findAll()).thenReturn(tags);
        List<?> result = tagService.getAllTags(userNotAdmin);
        Assertions.assertEquals(2, result.size());
        TagUserDto dto = (TagUserDto) result.get(0);
        Assertions.assertEquals("MockTag1", dto.getTagName());
    }
    @Test
    public void getById_Should_ReturnTag_When_Authorized() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");
        Tag mockTag = Helpers.createMockTag();
        when(mockTagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        Optional<Tag> result = tagService.getById(adminUser, 1);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("MockTagName", result.get().getTagName());
    }

    @Test
    public void getById_Should_Throw_When_NotAuthorized() {
        User nonAdminUser = Helpers.createMockUser();
        nonAdminUser.getRole().setRoleName("User");
        Assertions.assertThrows(AuthorizationException.class, () -> tagService.getById(nonAdminUser, 1));
    }
    @Test
    public void addTagToPost_Should_AddTag_When_Valid() {
        User mockUser = Helpers.createMockUser();
        mockUser.getRole().setRoleName("User");
        Post mockPost = Helpers.createMockPost();
        Tag mockTag = Helpers.createMockTag();
        when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        when(mockTagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        tagService.addTagToPost(1, 1, mockUser);
        Assertions.assertTrue(mockPost.getTags().contains(mockTag));
        Assertions.assertTrue(mockTag.getPosts().contains(mockPost));
        verify(mockPostRepository, times(1)).save(mockPost);
        verify(mockTagRepository, times(1)).save(mockTag);
    }
    @Test
    public void removeTagToPost_Should_RemoveTag_When_Valid() {
        User mockUser = Helpers.createMockUser();
        mockUser.getRole().setRoleName("User");
        Post mockPost = Helpers.createMockPost();
        Tag mockTag = Helpers.createMockTag();
        Set<Tag> tags = new HashSet<>();
        tags.add(mockTag);
        mockPost.setTags(tags);
        when(mockPostRepository.findById(1)).thenReturn(Optional.of(mockPost));
        when(mockTagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        tagService.removeTagToPost(1, 1, mockUser);
        Assertions.assertFalse(mockPost.getTags().contains(mockTag));
        Assertions.assertFalse(mockTag.getPosts().contains(mockPost));
        verify(mockPostRepository, times(1)).save(mockPost);
        verify(mockTagRepository, times(1)).save(mockTag);
    }

    @Test
    public void addTagToPost_Should_Throw_When_TagAlreadyAdded(){
        Post post = Helpers.createMockPost();
        Tag tag = Helpers.createMockTag();
        post.getTags().add(tag);
        Assertions.assertThrows(OperationAlreadyPerformedException.class,
                ()-> TagServiceImpl.checkForAlreadyDoneOperation(post, tag, true));
    }
    @Test
    public void removeTagToPost_Should_Throw_When_TagAlreadyAdded(){
        Post post = Helpers.createMockPost();
        Tag tag = Helpers.createMockTag();
        post.getTags().remove(tag);
        Assertions.assertThrows(OperationAlreadyPerformedException.class,
                ()->TagServiceImpl.checkForAlreadyDoneOperation(post, tag, false));
    }
    @Test
    public void checkAccessPermissions_Should_ThrowAuthorizationException_When_UserNotCreator() {
        User userNotCreator = new User();
        userNotCreator.setId(2);
        User postCreator = new User();
        postCreator.setId(1);
        Post post = new Post();
        post.setCreatedBy(postCreator);
        Assertions.assertThrows(AuthorizationException.class, () -> {
            TagServiceImpl.checkAccessPermissions(post, userNotCreator);
        });
    }
    @Test
    public void update_Should_UpdateTag_When_Authorized() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");
        Tag mockTag = Helpers.createMockTag();
        when(mockTagRepository.save(mockTag)).thenReturn(mockTag);
        Tag result = tagService.update(mockTag, adminUser);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("MockTagName", result.getTagName());
        verify(mockTagRepository, times(1)).save(mockTag);
    }
    @Test
    public void update_Should_Throw_When_NotAuthorized() {
        User nonAdminUser = Helpers.createMockUser();
        nonAdminUser.getRole().setRoleName("User");
        Tag mockTag = Helpers.createMockTag();
        Assertions.assertThrows(AuthorizationException.class, () -> tagService.update(mockTag, nonAdminUser));
    }
    @Test
    public void create_Should_CreateTag_When_Valid() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");
        Tag mockTag = Helpers.createMockTag();
        when(mockTagRepository.save(mockTag)).thenReturn(mockTag);
        Tag result = tagService.create(mockTag, adminUser);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("MockTagName", result.getTagName());
        verify(mockTagRepository, times(1)).save(mockTag);
    }

    @Test
    public void create_Should_Throw_When_DuplicateTag() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");
        Tag mockTag = Helpers.createMockTag();
        when(mockTagRepository.save(mockTag)).thenThrow(new DataIntegrityViolationException("Duplicate tag"));
        Assertions.assertThrows(DuplicateEntityException.class, () -> tagService.create(mockTag, adminUser));
    }

    @Test
    public void delete_Should_DeleteTag_When_Authorized() {
        User adminUser = Helpers.createMockUser();
        adminUser.getRole().setRoleName("Admin");
        Tag mockTag = Helpers.createMockTag();
        when(mockTagRepository.findById(1)).thenReturn(Optional.of(mockTag));
        tagService.delete(1, adminUser);
        verify(mockTagRepository, times(1)).delete(mockTag);
    }

    @Test
    public void delete_Should_Throw_When_NotAuthorized() {
        User nonAdminUser = Helpers.createMockUser();
        nonAdminUser.getRole().setRoleName("User");
        Assertions.assertThrows(AuthorizationException.class, () -> tagService.delete(1, nonAdminUser));
    }
}
