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

import static com.example.forumproject.helpers.AuthenticationHelper.MODIFY_ERROR_MESSAGE;

@Service
public class UserServiceImpl implements UserService {

    public static final String MODIFY_ERROR_MESSAGE_ADMIN = "Only admin can delete user";
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

    //TODO implement check for admin and also check if it is properly structured
    @Transactional
    public User save(User user, int id) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));
        User updatedUser = updateUser(user, userToUpdate);
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUserById(int id, User user) {
        checkModifyPermissions(user);
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

    private void checkModifyPermissions(User user) {
        if (user.getRole().getRoleName().equals("User")) {
            throw new AuthorizationException(MODIFY_ERROR_MESSAGE_ADMIN);
        }
    }

    private User updateUser(User inputUser, User userToUpdate) {
        if (inputUser.getPassword() != null) {
            userToUpdate.setPassword(inputUser.getPassword());
        }
        if (inputUser.getFirstName() != null) {
            userToUpdate.setFirstName(inputUser.getFirstName());
        }
        if (inputUser.getLastName() != null) {
            userToUpdate.setLastName(inputUser.getLastName());
        }
        if (inputUser.getBlocked() != null) {
            userToUpdate.setBlocked(inputUser.getBlocked());
        }

        return userToUpdate;
    }
}
