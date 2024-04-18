package com.mybank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OverdraftBankAccountTest {

    @Test
    public void testWithdrawValidAmount() {
        OverdraftBankAccount account = new OverdraftBankAccount(123456, "password", 1000, 500);
        boolean result = account.withdraw(500);
        Assertions.assertTrue(result);
        Assertions.assertEquals(500, account.getBalance());
    }

    @Test
    public void testWithdrawInvalidAmount() {
        OverdraftBankAccount account = new OverdraftBankAccount(123456, "password", 1000, 500);
        boolean result = account.withdraw(1500);
        Assertions.assertTrue(result);
        Assertions.assertEquals(-500, account.getBalance());
    }

    @Test
    public void testWithdrawNegativeAmount() {
        OverdraftBankAccount account = new OverdraftBankAccount(123456, "password", 1000, 500);
        boolean result = account.withdraw(-200);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
    }

    @Test
    public void testWithdrawAmountExceedsOverdraftLimit() {
        OverdraftBankAccount account = new OverdraftBankAccount(123456, "password", 1000, 500);
        boolean result = account.withdraw(2000);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
    }

    @Test
    public void testSetAndGetOverdraftLimit() {
        OverdraftBankAccount account = new OverdraftBankAccount(123456, "password", 1000, 500);
        account.setOverdraftLimit(1000);
        Assertions.assertEquals(1000, account.getOverdraftLimit());
    }
}