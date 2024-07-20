package com.example.forumproject.repositories.contracts;

import com.example.forumproject.models.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPhoneNumberRepository extends JpaRepository<UserPhoneNumber, Integer> {
}
