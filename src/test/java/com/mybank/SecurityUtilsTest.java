package com.mybank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecurityUtilsTest {

    @Test
    public void testHashPassword() {
        String password = "password123";
        String hashedPassword = SecurityUtils.hashPassword(password);
        System.out.println("Hashed password: " + hashedPassword);
        Assertions.assertNotNull(hashedPassword);
        Assertions.assertNotEquals(password, hashedPassword);
    }

    @Test
    public void testCheckPasswordValid() {
        String password = "password123";
        String hashedPassword = SecurityUtils.hashPassword(password);
        boolean result = SecurityUtils.checkPassword(hashedPassword, password);
        System.out.println("Check password valid result: " + result);
        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckPasswordInvalid() {
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = SecurityUtils.hashPassword(password);
        boolean result = SecurityUtils.checkPassword(hashedPassword, wrongPassword);
        System.out.println("Check password invalid result: " + result);
        Assertions.assertFalse(result);
    }
}