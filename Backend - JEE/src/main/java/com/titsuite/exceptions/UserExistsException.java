package com.titsuite.exceptions;

public class UserExistsException extends RuntimeException {

    private static final long serialVersionUID = 2481381224355081751L;

    public UserExistsException() { super(); }

    public UserExistsException(String user) {
        super("User is already registered: " + user);
    }

}
