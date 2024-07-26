package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.specifications.CommentSpecification;
import com.example.forumproject.models.Comment;
import com.example.forumproject.models.User;
import com.example.forumproject.models.options.FilterOptions;
import com.example.forumproject.repositories.contracts.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    CommentRepository mockCommentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    public void getById_Should_ReturnComment_When_MatchExists() {
        Comment mockComment = Helpers.createMockComment();
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(Optional.of(mockComment));

        Comment result = commentService.getById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    public void getById_Should_ThrowEntityNotFoundException_When_CommentDoesNotExist() {
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.getById(1));
    }

    @Test
    public void getAll_Should_ReturnFilteredComments() {
        FilterOptions mockFilterOptions = new FilterOptions("2", null, null, null, null, null);
        List<Comment> mockComments = List.of(Helpers.createMockComment(), Helpers.createMockComment(), Helpers.createMockComment());

        when(mockCommentRepository.findAll(any(Specification.class))).thenReturn(mockComments);

        List<Comment> result = commentService.getAll(mockFilterOptions);

        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void save_Should_SaveComment_When_Valid() {
        Comment mockComment = Helpers.createMockComment();
        Mockito.when(mockCommentRepository.save(mockComment)).thenReturn(mockComment);

        Comment result = commentService.save(mockComment);

        Assertions.assertNotNull(result);
    }

    @Test
    public void deleteCommentById_Should_CallDeleteById_When_ValidId() {
        doNothing().when(mockCommentRepository).deleteById(1);

        commentService.deleteCommentById(1);

        verify(mockCommentRepository, times(1)).deleteById(1);
    }

    @Test
    public void createComment_Should_SaveComment_When_Valid() {
        Comment mockComment = Helpers.createMockComment();
        Mockito.when(mockCommentRepository.save(mockComment)).thenReturn(mockComment);

        Comment result = commentService.createComment(mockComment);

        Assertions.assertNotNull(result);
    }

    @Test
    public void updateComment_Should_UpdateComment_When_Valid() {
        User mockUser = Helpers.createMockUser();
        Comment existingComment = Helpers.createMockComment();
        existingComment.setCreatedBy(mockUser);
        Comment inputData = new Comment();
        inputData.setContent("Updated Content");

        Mockito.when(mockCommentRepository.findById(1)).thenReturn(Optional.of(existingComment));
        Mockito.when(mockCommentRepository.save(existingComment)).thenReturn(existingComment);

        Comment result = commentService.updateComment(inputData, mockUser, 1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Content", result.getContent());
    }

    @Test
    public void updateComment_Should_ThrowAuthorizationException_When_UserDoesNotHavePermission() {
        User mockUser = Helpers.createMockUser();
        User differentUser = Helpers.createMockUser();
        differentUser.setId(2);
        Comment existingComment = Helpers.createMockComment();
        existingComment.setCreatedBy(differentUser);

        Comment inputData = new Comment();
        inputData.setContent("Updated Content");

        Mockito.when(mockCommentRepository.findById(1)).thenReturn(Optional.of(existingComment));

        Assertions.assertThrows(AuthorizationException.class, () -> commentService.updateComment(inputData, mockUser, 1));
    }
}
