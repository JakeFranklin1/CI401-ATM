package CI401.mybank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BankAccountTest {

    @Test
    public void testWithdrawValidAmount() {
        BankAccount account = new BankAccount(123456, "password", 1000);
        boolean result = account.withdraw(500);
        Assertions.assertTrue(result);
        Assertions.assertEquals(500, account.getBalance());
    }

    @Test
    public void testWithdrawInvalidAmount() {
        BankAccount account = new BankAccount(123456, "password", 1000);
        boolean result = account.withdraw(1500);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
    }
}