package com.titsuite.exceptions;

public class UnauthorizedDiplomaChange extends RuntimeException {

    private static final long serialVersionUID = 6760774048440085970L;

    public UnauthorizedDiplomaChange() { super(); }

    public UnauthorizedDiplomaChange(long id) {
        super("Unauthorized to perform requested operation on diploma: " + id);
    }

}
