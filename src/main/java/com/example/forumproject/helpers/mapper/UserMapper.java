package com.example.forumproject.helpers.mapper;

import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.in.UserBlockDto;
import com.example.forumproject.models.dtos.in.UserDto;
import com.example.forumproject.models.dtos.in.UserInDto;
import com.example.forumproject.models.dtos.out.UserOutDto;
import com.example.forumproject.services.contracts.RoleService;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleService roleService;
    private final UserService service;

    @Autowired
    public UserMapper(RoleService roleService, UserService service) {
        this.roleService = roleService;
        this.service = service;
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
    /*public UserInDto toDto(User user) {
        UserInDto dto = new UserInDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        return dto;
    }*/
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword("");
        userDto.setPasswordConfirm("");
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User fromDto(UserInDto dto, User existingUser) {
        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(dto.getPassword());
        }
        return existingUser;
    }
    public User fromDto(int id, UserDto dto) {
        User user = fromDto(dto);
        user.setId(id);
        User repositoryUser = service.getUserById(id).orElseThrow();
        user.setRole(repositoryUser.getRole());
        return user;
    }

    public User fromDto(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        return user;
    }


    public static UserOutDto toUserDto(User user) {
        return new UserOutDto(user.getFirstName(), user.getLastName(), user.getUsername());
    }
}
