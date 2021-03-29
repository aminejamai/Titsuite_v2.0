package com.titsuite.managers;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {

    public static String hashPassword(String originalPassword) {
        return BCrypt.hashpw(originalPassword, BCrypt.gensalt());
    }

    public static boolean validatePassword(String testPassword, String storedPassword) {
        return BCrypt.checkpw(testPassword, storedPassword);
    }

}
