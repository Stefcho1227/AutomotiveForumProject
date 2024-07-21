package com.example.forumproject.services;

import com.example.forumproject.models.User;
import com.example.forumproject.models.UserCreationDto;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.repositories.contracts.UserPhoneNumberRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int id) {
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

}
