package com.example.forumproject.services.contracts;

import com.example.forumproject.models.User;
import com.example.forumproject.models.UserPhoneNumber;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(int id);
    Optional<User> getByUsername(String username);


    User createUser(User user);

    void deleteUserById(int id, User user);

    UserPhoneNumber addPhoneNumber(int id, UserPhoneNumber userphoneNumber, User loggedInUser);

    void deletePhoneNumber(int id, User loggedInUser);

    User save(User user);

    User updateUser(User user, int id);

    User updateUserBlockStatus(User loggedInUser, User inputUser, int id);

}
