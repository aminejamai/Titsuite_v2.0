package com.titsuite.exceptions;

public class DiplomaNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6760774048440085970L;

    public DiplomaNotFoundException() { super(); }

    public DiplomaNotFoundException(long id) {
        super("Requested diploma doesn't exist: " + id);
    }

}
