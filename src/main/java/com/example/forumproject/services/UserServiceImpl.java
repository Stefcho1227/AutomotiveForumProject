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

    public static final String ERROR_MESSAGE_NO_PERMISSION = "You do not have permission to perform this operation";
    public static final String PHONE_NUMBER_ERROR_MESSAGE_NO_PERMISSION = "Only Admins have access to this functionality";
    public static final String ONLY_MODIFY_YOUR_PHONE_NUMBER = "You can only modify your own phone number";
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
        checkUserIsAdminOrModerator(user);
        userRepository.deleteById(id);
    }

    @Override
    public UserPhoneNumber addPhoneNumber(int id, UserPhoneNumber userPhoneNumber, User loggedInUser) {
        Optional<User> userOptional = userRepository.findById(id);
        checkUserIsAdmin(loggedInUser);
        checkIfUserIsHimself(loggedInUser, id, ONLY_MODIFY_YOUR_PHONE_NUMBER);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserIsAdmin(user);
            if (user.getPhoneNumber() != null) {
                user.setPhoneNumber(null);
            }
            userPhoneNumber.setUser(user);
            phoneNumberRepository.save(userPhoneNumber);
            return userPhoneNumber;
        }
        return null;
    }

    @Override
    public void deletePhoneNumber(int id, User loggedInUser) {
        Optional<User> userOptional = userRepository.findById(id);
        checkUserIsAdmin(loggedInUser);
        checkIfUserIsHimself(loggedInUser, id, ONLY_MODIFY_YOUR_PHONE_NUMBER);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPhoneNumber(null);
            userRepository.save(user);
        }

    }

    private void checkUserIsAdminOrModerator(User user) {
        if (user.getRole().getRoleName().equals("User")) {
            throw new AuthorizationException(ERROR_MESSAGE_NO_PERMISSION);
        }
    }

    private void checkUserIsAdmin(User user) {
        if (!user.getRole().getRoleName().equals("Admin")) {
            throw new AuthorizationException(PHONE_NUMBER_ERROR_MESSAGE_NO_PERMISSION);
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
        checkUserIsAdminOrModerator(loggedInUser);

        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));

        if (inputUser.getBlocked() != null) {
            userToUpdate.setBlocked(inputUser.getBlocked());
        }

        return userRepository.save(userToUpdate);
    }

    private void checkIfUserIsHimself(User loggedInUser, int id, String message) {
        if (loggedInUser.getId() != id) {
            throw new AuthorizationException(message);
        }
    }
}
