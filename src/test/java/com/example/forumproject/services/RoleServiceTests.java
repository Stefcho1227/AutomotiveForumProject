package com.example.forumproject.services;

import com.example.forumproject.Helpers;
import com.example.forumproject.models.Role;
import com.example.forumproject.repositories.contracts.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {
    @Mock
    RoleRepository mockRoleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    public void Get_All_Roles_Should_Return_All_Roles() {
        List<Role> roles = List.of(Helpers.createMockRole(), Helpers.createMockRole());

        Mockito.when(mockRoleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void Get_By_Id_Should_Return_Role_By_Id() {
        Role role = Helpers.createMockRole();

        Mockito.when(mockRoleRepository.findById(1)).thenReturn(Optional.of(role));

        Assertions.assertEquals(role.getId(), roleService.getRoleById(1).get().getId());
    }

}
