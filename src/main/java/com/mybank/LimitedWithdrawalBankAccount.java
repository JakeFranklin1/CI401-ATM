package com.mybank;


// LimitedWithdrawalBankAccount class extending BankAccount
public class LimitedWithdrawalBankAccount extends BankAccount {
    // Initialise the account specific variables.
    protected static final int MAX_WITHDRAWALS_PER_DAY = 3;
    protected int withdrawalsToday;

    // Constructor for the LimitedWithdrawalBankAccount class
    public LimitedWithdrawalBankAccount(int accNumber, String accPasswd, int balance) {
        super(accNumber, accPasswd, balance); // Call the constructor of the superclass (BankAccount)
        this.withdrawalsToday = 0; // Initialize the number of withdrawals made today to 0
        this.accountType = "limited"; // Set the account type to "limited"
    }

    public int getWithdrawalsLeft() {
        return MAX_WITHDRAWALS_PER_DAY - withdrawalsToday;
    }

    // Override withdraw method to limit withdrawals per day
    @Override
    public boolean withdraw(int amount) {
        Debug.trace("LimitedWithdrawalBankAccount::withdraw: amount =" + amount);

        if (amount < 0 || withdrawalsToday >= MAX_WITHDRAWALS_PER_DAY) {
            return false;
        } else {
            balance -= amount; // subtract amount from balance
            withdrawalsToday++;
            System.out.println("\n" + withdrawalsToday + "\n");
            return true;
        }
    }
}
