package com.mybank;
import java.io.*;
import java.util.*;


public class BankAccount {
    public int accNumber = 0;
    public String accPasswd = "";
    public int balance = 0;
    public String accountType = "normal";

    public BankAccount() {
    }

    public BankAccount(int a, String p, int b) {
        accNumber = a;
        accPasswd = p;
        balance = b;
    }

    public boolean withdraw(int amount) {
        Debug.trace("BankAccount::withdraw: amount = £" + amount);
        System.out.println("\n Printing from BankAccount Class \n");

        // CHANGE CODE HERE TO WITHDRAW MONEY FROM THE ACCOUNT
        if (amount < 0 || balance < amount) {
            return false;
        } else {
            balance = balance - amount; // subtract amount from balance
            return true;
        }
    }

    public boolean deposit(int amount) {
        Debug.trace("LocalBank::deposit: amount = £" + amount);
        // CHANGE CODE HERE TO DEPOSIT MONEY INTO THE ACCOUNT
        if (amount < 0) {
            return false;
        } else {
            balance = balance + amount; // add amount to balance
            return true;
        }
    }

    // Return the current balance in the account
    public int getBalance() {
        Debug.trace("LocalBank::getBalance");
        return balance;
    }

    public String getStatement() {
        Debug.trace("LocalBank::statement");
        List<String> lastFiveLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("CSV/transaction_history.csv"))) {
            String line;
            boolean isFirstLine = true; // Flag to skip the header line
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue; // Skip lines that don't have enough parts
                }
                // Check if the account number in the line matches the account number of the current account
                if (Integer.parseInt(parts[0]) != this.accNumber) {
                    continue; // Skip lines that are not for the current account
                }
                String formattedLine = "Transaction type: " + parts[1] + "\nAmount: " + Model.formatBalance(Integer.parseInt(parts[2])) + "\nNew Balance: " + Model.formatBalance(Integer.parseInt(parts[3]));
                lastFiveLines.add(formattedLine);
                if (lastFiveLines.size() > 5) {
                    lastFiveLines.remove(0); // Remove the oldest line if more than 5 lines
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if there are no transactions
        if (lastFiveLines.isEmpty()) {
            return "No transaction history available for this account.";
        }

        // Join the last 5 lines into a single string
        String statement = String.join("\n\n", lastFiveLines);
        return statement;
    }
}
