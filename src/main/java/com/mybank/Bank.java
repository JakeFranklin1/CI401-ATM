package com.mybank;

import java.io.*;
import java.util.*;

public class Bank {
    int maxAccounts = 10; // maximum number of accounts the bank can hold
    List<BankAccount> accounts = new ArrayList<>(); // list to hold the bank accounts
    BankAccount account = null; // currently logged in acccount ('null' if no-one is logged in)
    String accountsFile = "CSV/accounts.csv"; // CSV file to store accounts
    String transactionsFile = "CSV/transaction_history.csv"; // CSV file to store accounts

    public Bank() {
        Debug.trace("Bank::<constructor>");
        loadAccounts();
    }

    // Load accounts from CSV file
    private void loadAccounts() {
        Debug.trace("Bank::loadAccounts: Loading accounts from file");
        try (BufferedReader br = new BufferedReader(new FileReader(accountsFile))) {
            // Read the file line by line
            String line;
            boolean isFirstLine = true; // Flag to skip the header line

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                // Associate the given values with the account fields
                String[] values = line.split(",");
                try {
                    int accNumber = Integer.parseInt(values[0]);
                    String accPasswd = values[1]; // Decrypt the password here
                    int balance = Integer.parseInt(values[2]);
                    String accountType = values[3];

                    // Create the appropriate account type based on the accountType
                    switch (accountType) {
                        case "overdraft":
                            int overdraftLimit = Integer.parseInt(values[4]);
                            makeOverdraftBankAccount(accNumber, accPasswd, balance, overdraftLimit);
                            break;
                        case "limited":
                            makeLimitedWithdrawalBankAccount(accNumber, accPasswd, balance);
                            break;
                        default:
                            addBankAccount(accNumber, accPasswd, balance);
                            break;
                    }
                } catch (Exception e) {
                    Debug.trace("Bank::loadAccounts: Error parsing account data: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Debug.trace("Bank::loadAccounts: Error reading accounts file: " + e.getMessage());
        }
    }

    // Save accounts to CSV file
    private void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(accountsFile))) {
            String accountType = "";
            String extraInfo = "";

            pw.println("Account Number,Password,Balance,Account Type,Overdraft Limit"); // Header line
            for (BankAccount a : accounts) {
                if (a instanceof OverdraftBankAccount) {
                    accountType = "overdraft";
                    extraInfo = "," + ((OverdraftBankAccount) a).getOverdraftLimit();
                } else if (a instanceof LimitedWithdrawalBankAccount) {
                    accountType = "limited";
                    extraInfo = ",0";
                } else {
                    accountType = "normal";
                    extraInfo = ",0";
                }

                pw.println(a.accNumber + "," + a.accPasswd + "," + a.balance + "," + accountType + extraInfo);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logTransaction(int accNumber, String transactionType, int amount, int newBalance) {
        try {
            Debug.trace("Bank::logTransaction: Logging transaction for account %d", accNumber);
            File file = new File(transactionsFile);
            boolean isNewFile = file.createNewFile(); // This will create the file if it does not exist and return true
            // Open the file in append mode
            try (FileWriter csvWriter = new FileWriter(file, true)) {
                // Write the headers if the file is new
                if (isNewFile) {
                    csvWriter.append("accNumber,transactionType,amount,newBalance\n"); // Write the headers
                }

                String csvLine = String.format("%d,%s,%d,%d\n", accNumber, transactionType, amount, newBalance);
                // Append the transaction to the file
                csvWriter.append(csvLine);
                // Flush the writer to ensure the data is written to the file and not stored in
                // memory
                csvWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addBankAccount(BankAccount a) {
        Debug.trace("Bank::addBankAccount: Adding bank account %d", a.accNumber);
        if (accounts.size() < maxAccounts) {
            accounts.add(a);
            Debug.trace("Bank::addBankAccount: added Account:" + a.accNumber + " Balance: £" + a.balance);
            saveAccounts();
            return true;
        } else {
            Debug.trace("Bank::addBankAccount: can't add bank account - too many accounts");
            return false;
        }
    }

    // Rest of your methods...

    public boolean addBankAccount(int accNumber, String accPasswd, int balance) {
        return addBankAccount(makeBankAccount(accNumber, accPasswd, balance));
    }

    // Method to create OverdraftBankAccount and add it to the bank
    public void makeOverdraftBankAccount(int accNumber, String accPasswd, int balance, int overdraftLimit) {
        OverdraftBankAccount account = new OverdraftBankAccount(accNumber, accPasswd, balance, overdraftLimit);
        addBankAccount(account);
    }

    private BankAccount makeBankAccount(int accNumber, String accPasswd, int balance) {
        return new BankAccount(accNumber, accPasswd, balance);
    }

    // Method to create LimitedWithdrawalBankAccount and add it to the bank
    public void makeLimitedWithdrawalBankAccount(int accNumber, String accPasswd, int balance) {
        LimitedWithdrawalBankAccount account = new LimitedWithdrawalBankAccount(accNumber, accPasswd, balance);
        addBankAccount(account);
    }

    public boolean login(int newAccNumber, String newAccPasswd) {
        Debug.trace("Bank::login: Attempting to login with account %d", newAccNumber);
        logout();
    
        for (BankAccount b : accounts) {
            if (b.accNumber == newAccNumber) {
                String storedPasswordHash = b.accPasswd;
                boolean passwordMatches = SecurityUtils.checkPassword(storedPasswordHash, newAccPasswd);
                if (passwordMatches) {
                    Debug.trace("Bank::login: logged in, accNumber = " + newAccNumber + " balance = " + b.getBalance());
                    account = b;
                }
                return passwordMatches;
            }
        }
        account = null;
        return false;
    }

    // Reset the bank to a 'logged out' state
    public void logout() {
        if (loggedIn()) {
            Debug.trace("Bank::logout: logging out, accNumber = " + account.accNumber);
            account = null;
        }
    }

    // test whether the bank is logged in to an account or not
    public boolean loggedIn() {
        if (account == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deposit(int amount) {
        if (loggedIn()) {
            Debug.trace("Bank::deposit: Depositing %d", amount);
            boolean result = account.deposit(amount);
            if (result) {
                saveAccounts();
                logTransaction(account.accNumber, "deposit", amount, account.getBalance());
            }
            return result;
        } else {
            return false;
        }
    }

    public boolean withdraw(int amount) {
        if (loggedIn()) {
            Debug.trace("Bank::withdraw: Withdrawing %d", amount);
            boolean result = account.withdraw(amount);
            if (result) {
                saveAccounts();
                logTransaction(account.accNumber, "withdraw", amount, account.getBalance());
            }
            return result;
        } else {
            return false;
        }
    }

    public int getBalance() {
        if (loggedIn()) {
            Debug.trace("Bank::getBalance: Getting balance");
            return account.getBalance();
        } else {
            Debug.trace("Bank::getBalance: ERROR getting balance - not logged in");
            return -1; // use -1 as an indicator of an error
        }
    }

    public boolean transfer(int sourceAccNumber, int targetAccNumber, int amount) {
        // Check if a user is logged in
        if (loggedIn()) {
            // Log the transfer details
            Debug.trace(
                    "Bank::transfer: Transferring " + amount + " from " + sourceAccNumber + " to " + targetAccNumber);
            // Initialize source and target account objects
            BankAccount sourceAccount = null;
            BankAccount targetAccount = null;

            // Iterate over all accounts to find the source and target accounts
            for (BankAccount acc : accounts) {
                if (acc != null) {
                    if (acc.accNumber == sourceAccNumber) {
                        sourceAccount = acc; // Found the source account
                    } else if (acc.accNumber == targetAccNumber) {
                        targetAccount = acc; // Found the target account
                    }
                }
                // Break the loop if both accounts are found
                if (sourceAccount != null && targetAccount != null) {
                    break;
                }
            }
            // Proceed if both accounts are found
            if (sourceAccount != null && targetAccount != null) {
                // Attempt to withdraw the amount from the source account
                if (sourceAccount.withdraw(amount)) {
                    // If withdrawal is successful, deposit the amount to the target account
                    targetAccount.deposit(amount);

                    // Save the state of all accounts, log the transactions for both accounts
                    saveAccounts();
                    logTransaction(sourceAccount.accNumber, "transfer", amount, sourceAccount.getBalance());
                    logTransaction(targetAccount.accNumber, "transfer", amount, targetAccount.getBalance());
                    // Return true indicating the transfer was successful
                    return true;
                }
            }
        }
        // Return false if transfer was not successful or user is not logged in
        return false;
    }

    public boolean updatePassword(int accNumber, String newPassword) {
        Debug.trace("Bank::updatePassword: Attempting to update password for account %d", accNumber);
        for (BankAccount acc : accounts) {
            if (acc != null && acc.accNumber == accNumber) {
                Debug.trace("Bank::updatePassword: Found account %d", accNumber);
                String newHashedPassword = SecurityUtils.hashPassword(newPassword);
                acc.accPasswd = newHashedPassword;
                Debug.trace("Bank::updatePassword: Successfully updated password");
                saveAccounts();
                Debug.trace("Bank::updatePassword: Saved accounts to file");
                return true; // Password updated successfully
            }
        }
        Debug.trace("Bank::updatePassword: Failed to find account %d", accNumber);
        return false; // Account not found or password not updated
    }

    public String getStatement() {
        if (loggedIn()) {
            Debug.trace("Bank::getStatement: Getting statement");
            return account.getStatement();
        } else {
            Debug.trace("Bank::getStatement: ERROR getting statement - not logged in");
            return "ERROR: Not logged in";
        }
    }
}