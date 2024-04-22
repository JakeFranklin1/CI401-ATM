package CI401.mybank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTest {

    private Bank bank;

    @BeforeEach
    public void setup() {
        bank = new Bank();
    }

    @Test
    public void testAddBankAccount_MaxAccountsReached() {
        // Add maximum number of accounts to the bank
        for (int i = 0; i < bank.maxAccounts; i++) {
            BankAccount account = new BankAccount(i, "password", 1000);
            bank.addBankAccount(account);
        }

        // Try to add one more account
        BankAccount newAccount = new BankAccount(999999, "password", 1000);
        boolean result = bank.addBankAccount(newAccount);
        Assertions.assertFalse(result);
    }

    @Test
    public void testDeposit_NotLoggedIn() {
        BankAccount account = new BankAccount(123456, "password", 1000);
        bank.addBankAccount(account);

        boolean result = bank.deposit(500);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
    }

    @Test
    public void testWithdraw_NotLoggedIn() {
        BankAccount account = new BankAccount(123456, "password", 1000);
        bank.addBankAccount(account);

        boolean result = bank.withdraw(500);
        Assertions.assertFalse(result);
        Assertions.assertEquals(1000, account.getBalance());
    }

}