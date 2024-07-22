package com.example.forumproject.helpers;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.repositories.contracts.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;
    @Autowired
    public UserMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public  User fromDto(UserInDto inputData) {
        User user = new User();

        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        user.setUsername(inputData.getUsername());
        user.setPassword(inputData.getPassword());
        user.setBlocked(inputData.isBlocked());
        user.setRole(roleRepository.findById(inputData.getRoleId()).orElseThrow(() -> new EntityNotFoundException("Role", inputData.getRoleId())));

        return user;
    }
}
