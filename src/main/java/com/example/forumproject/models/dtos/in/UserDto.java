package com.example.forumproject.models.dtos.in;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotEmpty(message = "Username can't be empty!")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters long!")
    private String username;
    @NotEmpty(message = "Password can't be empty!")
    @Size(min = 5, message = "Password must be more than 5 characters long!")
    private String password;

    @NotEmpty(message = "You must confirm your password!")
    private String passwordConfirm;
    @NotEmpty(message = "First name can't be empty!")
    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters long!")
    private String firstName;
    @NotEmpty(message = "Last name can't be empty!")
    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters long!")
    private String lastName;

    @NotEmpty(message = "Email can't be empty!")
    private String email;

    public UserDto() {
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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}