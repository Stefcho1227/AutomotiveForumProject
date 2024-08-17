package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Role;
import com.example.forumproject.models.User;
import com.example.forumproject.repositories.contracts.RoleRepository;
import com.example.forumproject.repositories.contracts.UserPhoneNumberRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    UserRepository mockUserRepository;
    @Mock
    RoleRepository roleRepository;

    @Mock
    UserPhoneNumberRepository mockPhoneNumberRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void getAllUsers_Should_ReturnListOfUsers() {
        List<User> mockUsers = List.of(Helpers.createMockUser(), Helpers.createMockUser(), Helpers.createMockUser());
        Mockito.when(mockUserRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void getUserById_Should_ReturnUser_When_MatchExists() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.getUserById(1);

        Assertions.assertEquals(mockUser.getId(), result.get().getId());
    }

    @Test
    public void getByUsername_Should_ReturnUser_When_MatchExists() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserRepository.findByUsername("MockUsername")).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.getByUsername("MockUsername");

        Assertions.assertEquals("MockUsername", result.get().getUsername());
    }

    @Test
    public void createUser_Should_SaveUser_When_Valid() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);

        User result = userService.createUser(mockUser);

        Assertions.assertNotNull(result);
    }

    @Test
    public void createUser_Should_ThrowDuplicateEntityException_When_UserAlreadyExists() {
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockUserRepository.save(mockUser)).thenThrow(new DataIntegrityViolationException(""));

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.createUser(mockUser));
    }

    @Test
    public void deleteUserById_Should_ThrowAuthorizationException_When_UserIsNotAdmin() {
        User mockUser = Helpers.createMockUser();
        Role userRole = Helpers.createMockRole();
        mockUser.setRole(userRole);

        Assertions.assertThrows(AuthorizationException.class, () -> userService.deleteUserById(1, mockUser));
    }

    @Test
    public void deleteUserById_Should_DeleteUser_When_UserIsAdmin() {
        User adminUser = Helpers.createMockUser();
        Role adminRole = Helpers.createMockRole();
        adminRole.setId(1);
        adminRole.setRoleName("Admin");
        adminUser.setRole(adminRole);

        User mockUser = new User();
        Mockito.doNothing().when(mockUserRepository).deleteById(1);

        userService.deleteUserById(1, adminUser);

        verify(mockUserRepository, times(1)).deleteById(1);
    }

    @Test
    public void addPhoneNumber_Should_AddPhoneNumber_When_UserExists() {

    }

    @Test
    public void addPhoneNumber_Should_ReturnNull_When_UserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.empty());

//        UserPhoneNumber result = userService.addPhoneNumber(1, "1234567890");

//        Assertions.assertNull(result);
    }

    @Test
    public void updateUser_Should_UpdateUser_When_Valid() {
        User existingUser = Helpers.createMockUser();
        User inputUser = new User();
        inputUser.setPassword("newPassword");

        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(mockUserRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(inputUser, 1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("newPassword", existingUser.getPassword());
    }

    @Test
    public void updateUser_Should_ThrowEntityNotFoundException_When_UserDoesNotExist() {
        User inputUser = Helpers.createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.updateUser(inputUser, 1));
    }

    @Test
    public void updateUserBlockStatus_Should_UpdateBlockStatus_When_Admin() {
        User adminUser = Helpers.createMockUser();
        Role adminRole = Helpers.createMockRole();
        adminRole.setId(1);
        adminRole.setRoleName("Admin");
        adminUser.setRole(adminRole);

        User userToUpdate = new User();
        User inputUser = new User();
        inputUser.setIsBlocked(true);

        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(userToUpdate));
        Mockito.when(mockUserRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User result = userService.updateUserBlockStatus(adminUser, inputUser, 1);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(userToUpdate.getIsBlocked());
    }

    @Test
    public void updateUserBlockStatus_Should_ThrowAuthorizationException_When_NotAdmin() {
        User mockUser = Helpers.createMockUser();
        mockUser.setRole(Helpers.createMockRole());

        Assertions.assertThrows(AuthorizationException.class, () -> userService.updateUserBlockStatus(mockUser, new User(), 1));
    }

    @Test
    public void save_Should_SaveUser_When_Valid() {
        User mockUser = Helpers.createMockUser();

        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);

        Assertions.assertSame(mockUser, userService.save(mockUser));
    }

}
