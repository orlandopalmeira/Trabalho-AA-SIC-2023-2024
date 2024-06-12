package com.grupo6.votingapp.exceptions.votingservice;

public class UserAlreadyVotedException extends Exception {
    public UserAlreadyVotedException(String message) {
        super(message);
    }
}
