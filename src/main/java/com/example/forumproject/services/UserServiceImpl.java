package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.repositories.contracts.UserPhoneNumberRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.forumproject.helpers.AuthenticationHelper.MODIFY_ERROR_MESSAGE;

@Service
public class UserServiceImpl implements UserService {

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
    public Optional<User> getByUsername(String username){
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

    public User save(User user) {
        return userRepository.save(user);
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
    //TODO
    //WILL IT BE BETTER TO FIND IT BY NAME NOT ID BECAUSE OF THE DATABASE
    //beer.getCreatedBy().equals(user) maybe if we add this is will be for better bonus authentication
    private void checkModifyPermissions(User user) {
        if (user.getRole().getId() != 1) {
            throw new AuthorizationException(MODIFY_ERROR_MESSAGE);
        }
    }

}
