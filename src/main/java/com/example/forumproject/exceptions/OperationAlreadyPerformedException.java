package com.example.forumproject.exceptions;

public class OperationAlreadyPerformedException extends RuntimeException {
    public OperationAlreadyPerformedException(String message){
        super(message);
    }
}
