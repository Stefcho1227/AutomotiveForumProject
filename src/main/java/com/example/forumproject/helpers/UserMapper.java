package com.example.forumproject.helpers;

import com.example.forumproject.models.Role;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserCreationDto;
import com.example.forumproject.models.UserPhoneNumber;

public class UserMapper {

    public static User fromDto(UserCreationDto inputData) {
        User user = new User();

        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        user.setUsername(inputData.getUsername());
        user.setPassword(inputData.getPassword());
        user.setBlocked(inputData.isBlocked());
        user.setPhoneNumber(new UserPhoneNumber(inputData.getPhoneNumber(), user));
        user.setRole(new Role(1, "tgsgse"));
        //TODO
        return user;
    }
}
