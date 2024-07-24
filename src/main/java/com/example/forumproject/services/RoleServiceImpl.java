package com.example.forumproject.services;

import com.example.forumproject.models.Role;
import com.example.forumproject.repositories.contracts.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;
    @Autowired
    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Role> getAllRoles() {
       return repository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(int id) {
        return repository.findById(id);
    }
}
