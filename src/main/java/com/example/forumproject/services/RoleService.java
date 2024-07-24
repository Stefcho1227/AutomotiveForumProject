package com.example.forumproject.services;

import com.example.forumproject.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> getRoleById(int id);
}
