package com.twitter.monolith.exception;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super("El nombre \"" + username + "\" ya está en uso. Elige otro.");
    }
}
