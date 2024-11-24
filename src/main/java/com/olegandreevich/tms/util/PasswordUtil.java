package com.olegandreevich.tms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    @Autowired
    private static BCryptPasswordEncoder bCryptPasswordEncoder;

    public static String encodePassword(String plainTextPassword) {
        return bCryptPasswordEncoder.encode(plainTextPassword);
    }
}
