package com.example.forumproject.exceptions;

public class BlockedException extends RuntimeException{
    public BlockedException(String attribute, String value){
        super(String.format("User with %s %s can only view posts and comments and view own profile", attribute, value));
    }
}
