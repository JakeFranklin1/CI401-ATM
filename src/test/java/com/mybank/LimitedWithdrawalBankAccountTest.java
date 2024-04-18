package com.mybank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LimitedWithdrawalBankAccountTest {

    @Test
    public void testWithdrawValidAmount() {
        LimitedWithdrawalBankAccount account = new LimitedWithdrawalBankAccount(123456, "password", 1000);
        boolean result = account.withdraw(500);
        Assertions.assertTrue(result);
        Assertions.assertEquals(500, account.getBalance());
        Assertions.assertEquals(2, account.getWithdrawalsLeft());
    }

    @Test
    public void testWithdrawInvalidAmount() {
        LimitedWithdrawalBankAccount account = new LimitedWithdrawalBankAccount(123456, "password", 1000);
        boolean result = account.withdraw(1500);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
        Assertions.assertEquals(3, account.getWithdrawalsLeft());
    }

    @Test
    public void testWithdrawExceedMaxWithdrawals() {
        LimitedWithdrawalBankAccount account = new LimitedWithdrawalBankAccount(123456, "password", 1000);
        account.withdraw(200);
        account.withdraw(300);
        account.withdraw(400);
        boolean result = account.withdraw(100);
        Assertions.assertFalse(result);
        Assertions.assertEquals(100, account.getBalance());
        Assertions.assertEquals(0, account.getWithdrawalsLeft());
    }
}