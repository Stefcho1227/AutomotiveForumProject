package com.example.forumproject.helpers.mapper;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.UserBlockDto;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.models.dtos.out.UserOutDto;
import com.example.forumproject.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleService roleService;

    @Autowired
    public UserMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public User fromDto(UserInDto inputData) {
        User user = new User();

        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        user.setUsername(inputData.getUsername());
        user.setPassword(inputData.getPassword());
        user.setBlocked(false);
        user.setRole(roleService.getRoleById(inputData.getRoleId()).orElseThrow(() -> new EntityNotFoundException("Role", inputData.getRoleId())));

        return user;
    }

    public User fromBlockedDto(UserBlockDto inputData) {
        User user = new User();
        if (inputData.getData().equals("block")) {
            user.setBlocked(true);
        } else if (inputData.getData().equals("unblock")) {
            user.setBlocked(false);
        }
        return user;
    }

    public static UserOutDto toUserDto(User user) {
        return new UserOutDto(user.getFirstName(), user.getLastName(), user.getUsername());
    }
}
