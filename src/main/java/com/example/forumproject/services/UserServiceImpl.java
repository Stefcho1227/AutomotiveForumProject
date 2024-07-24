package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.repositories.contracts.UserPhoneNumberRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    public static final String ERROR_MESSAGE_NOT_ADMIN = "Only an admin can perform this operation";
    private final UserRepository userRepository;
    private final UserPhoneNumberRepository phoneNumberRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserPhoneNumberRepository phoneNumberRepository) {
        this.userRepository = userRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException("User with email/username already exists.");
        }
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int id, User user) {
        checkUserPermission(user);
        userRepository.deleteById(id);
    }

    @Override
    public UserPhoneNumber addPhoneNumber(int id, String phoneNumber) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserPhoneNumber userPhoneNumber = new UserPhoneNumber();
            userPhoneNumber.setValue(phoneNumber);
            userPhoneNumber.setUser(user);
            user.setPhoneNumber(userPhoneNumber);
            phoneNumberRepository.save(userPhoneNumber);
            return userPhoneNumber;
        }
        return null;
    }

    private void checkUserPermission(User user) {
        if (user.getRole().getRoleName().equals("User")) {
            throw new AuthorizationException(ERROR_MESSAGE_NOT_ADMIN);
        }
    }

    @Override
    public User updateUser(User inputUser, int id) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));

        if (inputUser.getPassword() != null) {
            userToUpdate.setPassword(inputUser.getPassword());
        }
        if (inputUser.getFirstName() != null) {
            userToUpdate.setFirstName(inputUser.getFirstName());
        }
        if (inputUser.getLastName() != null) {
            userToUpdate.setLastName(inputUser.getLastName());
        }


        return userRepository.save(userToUpdate);
    }

    @Override
    public User updateUserBlockStatus(User loggedInUser, User inputUser, int id) {
        checkUserPermission(loggedInUser);

        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));

        if (inputUser.getBlocked() != null) {
            userToUpdate.setBlocked(inputUser.getBlocked());
        }

        return userRepository.save(userToUpdate);
    }
}
