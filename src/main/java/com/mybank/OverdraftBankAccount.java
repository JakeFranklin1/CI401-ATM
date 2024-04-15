package com.mybank;


public class OverdraftBankAccount extends BankAccount {
    private int overdraftLimit;

    public OverdraftBankAccount(int accNumber, int accPasswd, int balance, int overdraftLimit) {
        super(accNumber, accPasswd, balance);
        this.overdraftLimit = overdraftLimit;
        this.accountType = "overdraft"; // Update this field
    }

    @Override
    public boolean withdraw(int amount) {
        Debug.trace("OverdraftBankAccount::withdraw: amount =" + amount);
        if (amount < 0 || (balance + overdraftLimit) < amount) {
            return false;
        } else {
            balance -= amount; // subtract amount from balance
            return true;
        }
    }

    // Additional method to set overdraft limit
    public void setOverdraftLimit(int overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    // Additional method to get overdraft limit
    public int getOverdraftLimit() {
        return overdraftLimit;
    }
}