package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.helpers.specifications.UserMvcSpecification;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserPhoneNumber;
import com.example.forumproject.repositories.contracts.UserPhoneNumberRepository;
import com.example.forumproject.repositories.contracts.UserRepository;
import com.example.forumproject.services.contracts.RoleService;
import com.example.forumproject.services.contracts.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";
    public static final String PHONE_NUMBER_ERROR_NO_PERMISSION_MESSAGE = "Only Admins have access to this functionality";
    public static final String NO_PHONE_NUMBER_MODIFICATION_PERMISSION_MESSAGE = "You can only modify your own phone number";
    private final UserRepository userRepository;
    private final UserPhoneNumberRepository phoneNumberRepository;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserPhoneNumberRepository phoneNumberRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.roleService = roleService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllUsers(String firstName, String email, String username) {
        Specification<User> spec = Specification.where(null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and(UserMvcSpecification.hasFirstName(firstName));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(UserMvcSpecification.hasEmail(email));
        }
        if (username != null && !username.isEmpty()) {
            spec = spec.and(UserMvcSpecification.hasUsername(username));
        }


        return userRepository.findAll(spec);
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
        //TODO username cheack
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int id, User user) {
        throwIfUserIsNotAdminOrModerator(user);
        userRepository.deleteById(id);
    }

    @Override
    public UserPhoneNumber addPhoneNumber(int id, UserPhoneNumber userPhoneNumber, User loggedInUser) {
        throwIfUserIsNotAdmin(loggedInUser);
        throwIfUserIsNotHimself(loggedInUser, id, NO_PHONE_NUMBER_MODIFICATION_PERMISSION_MESSAGE);
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            throwIfUserIsNotAdmin(user);
            if (user.getPhoneNumber() != null) {
                user.setPhoneNumber(null);
            }
            userPhoneNumber.setUser(user);
            phoneNumberRepository.save(userPhoneNumber);
            return userPhoneNumber;
        }
        return null;
        //TODO make it not return null but throw an exception        findById or throw
    }

    @Override
    public void deletePhoneNumber(int id, User loggedInUser) {
        Optional<User> userOptional = userRepository.findById(id);
        throwIfUserIsNotAdmin(loggedInUser);
        throwIfUserIsNotHimself(loggedInUser, id, NO_PHONE_NUMBER_MODIFICATION_PERMISSION_MESSAGE);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPhoneNumber(null);
            userRepository.save(user);
        }
        //TODO same as above
    }

    private void throwIfUserIsNotAdminOrModerator(User user) {
        if (user.getRole().getRoleName().equals("User")) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

    private void throwIfUserIsNotAdmin(User user) {
        if (!user.getRole().getRoleName().equals("Admin")) {
            throw new AuthorizationException(PHONE_NUMBER_ERROR_NO_PERMISSION_MESSAGE);
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
        if (inputUser.getEmail() != null) {
            userToUpdate.setEmail(inputUser.getEmail());
        }


        return userRepository.save(userToUpdate);
    }

    @Override
    public User userAddPrivileges(int id) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));
        userToUpdate.setRole(roleService.getRoleById(1).get());
        return userRepository.save(userToUpdate);
    }

    @Override
    public User userRemovePrivileges(int id) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));
        userToUpdate.setRole(roleService.getRoleById(3).get());
        return userRepository.save(userToUpdate);
    }

    @Override
    public User updateUserBlockStatus(User loggedInUser, User inputUser, int id) {
        throwIfUserIsNotAdminOrModerator(loggedInUser);

        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));

        if (inputUser.getIsBlocked() != null) {
            userToUpdate.setIsBlocked(inputUser.getIsBlocked());
        }

        return userRepository.save(userToUpdate);
    }

    private void throwIfUserIsNotHimself(User loggedInUser, int id, String message) {
        if (loggedInUser.getId() != id) {
            throw new AuthorizationException(message);
        }
    }
}
