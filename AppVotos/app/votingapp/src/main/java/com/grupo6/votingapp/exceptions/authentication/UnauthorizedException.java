package com.grupo6.votingapp.exceptions.authentication;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
