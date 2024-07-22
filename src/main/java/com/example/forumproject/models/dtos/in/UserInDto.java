package com.example.forumproject.models.dtos.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserInDto {
    @NotNull(message = "Name can't be empty")
    @Size(min = 4, max = 32, message = "Name should be between 16 and 64 symbols")
    private String firstName;
    @NotNull(message = "Name can't be empty")
    @Size(min = 4, max = 32, message = "Name should be between 16 and 64 symbols")
    private String lastName;
    private String email;
    private String username;
    private String password;

    private int roleId;

    private boolean isBlocked;

    private String phoneNumber;

    public UserInDto() {
        this.isBlocked = false;
        this.roleId = 3;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
