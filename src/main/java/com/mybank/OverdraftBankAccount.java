package com.mybank;

/**
 * Represents an overdraft bank account that extends the BankAccount class.
 * It overrides the withdraw method to allow for overdrafts.
 * It also adds an overdraft limit field and methods to set and get the
 * overdraft limit.
 */
public class OverdraftBankAccount extends BankAccount {
    private int overdraftLimit;

    /**
     * Constructs an overdraft bank account with the specified account number,
     * password, balance, and overdraft limit.
     *
     * @param accNumber      the account number
     * @param accPasswd      the account password
     * @param balance        the account balance
     * @param overdraftLimit the overdraft limit
     */
    public OverdraftBankAccount(int accNumber, String accPasswd, int balance, int overdraftLimit) {
        super(accNumber, accPasswd, balance);
        this.overdraftLimit = overdraftLimit;
        this.accountType = "overdraft"; // Update this field
    }

    /**
     * Withdraws the specified amount from the account.
     *
     * @param amount the amount to withdraw
     * @return true if the withdrawal is successful, false otherwise
     */
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

    /**
     * Sets the overdraft limit for the account.
     *
     * @param overdraftLimit the overdraft limit to set
     */
    public void setOverdraftLimit(int overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Gets the overdraft limit of the account.
     *
     * @return the overdraft limit
     */
    public int getOverdraftLimit() {
        return overdraftLimit;
    }
}