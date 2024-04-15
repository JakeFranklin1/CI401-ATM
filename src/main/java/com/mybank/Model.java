package com.mybank;

// This class represents the Model in the Model-View-Controller pattern.

public class Model {

    // The Controller instance associated with this Model.
    public Controller controller;
    // The View instance associated with this Model.
    public View view;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // Method to set the View instance
    public void setView(View view) {
        this.view = view;
    }

    // Constants representing different states of the ATM.
    public final String LOGGED_IN = "logged_in";
    final String CHANGE_PASSWORD = "change_password";
    final String WITHDRAWING = "withdrawing";
    final String DEPOSITING = "depositing";
    final String LOGGED_OUT = "logged_out";
    final String TRANSFERRING = "transferring";
    final String ENTERING_ACCOUNT = "enteringAccount";

    public static final int PASSWORD_INCORRECT = 1;
    public static final int PASSWORD_MISMATCH = 2;
    public static final int PASSWORD_SAME = 3;
    public static final int PASSWORD_UPDATED = 0;

    // variables representing the ATM model
    String state = LOGGED_IN; // The current state of the ATM.
    int number = 0; // The current number displayed in the GUI.
    Bank bank = null; // The Bank object that the ATM communicates with.
    int targetAccountNumber = -1; // The target account number for a transfer operation.
    int accNumber = -1; // The account number entered by the user.
    int accPasswd = -1; // The password entered by the user.
    int newPassword = -1; // The new password entered by the user during password change.
    int missing_funds = 0; // The amount of funds missing during a withdrawal operation.
    String display1 = null; // The contents of the first message box in the GUI.
    String display2 = null; // The contents of the second message box in the GUI.

    // The other parts of the model-view-controller setup

    // Constructor for the Model class.
    public Model(Bank b) {
        Debug.trace("Model::<constructor>");
        bank = b;
    }

    public void setAccount(int accNumber, int accPasswd) {
        this.accNumber = accNumber;
        this.accPasswd = accPasswd;
    }

    // Method to get the current account, used to decide which scene to show when "Account" is clicked
    // Used in the Controller class.
    public BankAccount getCurrentAccount() {
        return bank.account;
    }

    // Method to initialize the ATM.
    public void initialise(String message) {
        Debug.trace("model::initialise");
        setState(LOGGED_IN);
        number = 0;
        display1 = "";
        display2 = message + "\nPlease choose from one of the six options.";
        display();
    }

    // Method to change the state of the ATM.
    public void setState(String newState) {
        if (!state.equals(newState)) {
            String oldState = state;
            state = newState;
            Debug.trace("Model::setState: changed state from " + oldState + " to " + newState);
        }
    }

    public void processNumber(String label) {
        // Process each character in the label, I changed this slightly from
        // the original code so that it handles integer overflow and can also handle
        // the "00" button using toCharArray()
        for (char c : label.toCharArray()) {
            if (number <= Integer.MAX_VALUE / 10) { // Check if next operation will overflow
                number = number * 10 + c - '0'; // Build number
            }
        }
        // show the new number in the display with a £ in front
        // only if the state is not ENTERING_ACCOUNT
        if (!state.equals(ENTERING_ACCOUNT)) {
            display1 = "£" + number;
        } else {
            display1 = "" + number;
        }
        display(); // update the GUI
    }

    // process the Clear button - reset the number (and number display string)
    public void processClear() {
        // clear the number stored in the model
        number = 0;
        display1 = "";
        display(); // update the GUI
    }

    public void processEnter() {
        // Enter was pressed - what we do depends what state the ATM is already in
        switch (state) {
        case WITHDRAWING:
            makeWithDrawal();
            break;
        case DEPOSITING:
            makeDeposit();
            break;
        case ENTERING_ACCOUNT:
            targetAccountNumber = number;
            makeTransfer();
            break;
        case TRANSFERRING:
            makeTransfer();
            break;
        case LOGGED_OUT:
            // I just put this here to ensure the user gets logged out if the program glitches
            // And they somehow manage to press enter after clicking the logout button
            controller.process("LOGOUT");
            break;
        case LOGGED_IN:
        default:
            // do nothing in any other state (ie logged in)
        }
        display(); // update the GUI
    }

    public int changePassword(String currentPassword, String newPassword, String confirmPassword) {
        Debug.trace("Model::changePassword");
        // Check if the current password is correct
        if (!currentPassword.equals(Integer.toString(accPasswd))) {
            return PASSWORD_INCORRECT; // Current password is incorrect
        }
        // Check if the new password matches the confirmed password
        if (!newPassword.equals(confirmPassword)) {
            return PASSWORD_MISMATCH; // New password and confirmed password do not match
        }
        // Check if the new password matches the current password
        if (newPassword.equals(currentPassword)) {
            return PASSWORD_SAME; // New password is the same as the current password
        }
        // If none of the error conditions are met, change the password
        Debug.trace("Model::changePassword: changing password: " + accPasswd + " to " + newPassword);
        accPasswd = Integer.parseInt(newPassword); // Update the password in the model
        bank.updatePassword(accNumber, Integer.parseInt(newPassword)); // Update the password in the bank
        return PASSWORD_UPDATED; // Password updated successfully
    }

    public int changeOverdraft(String overdraft) {
        Debug.trace("Model::changeOverdraft");
        // Check if the overdraft is a positive number
        if (overdraft.matches("^[0-9]+$")) {
            int overdraftInt = Integer.parseInt(overdraft);

            // Check if the overdraft is greater than the current balance only if the
            // account is in its overdraft
            if (bank.getBalance() < 0 && overdraftInt <= bank.getBalance()) {
                return 2; // Overdraft must be greater than the current balance
            }

            // Check if the account has an overdraft facility
            if (bank.account instanceof OverdraftBankAccount) {
                // Change the overdraft successfully
                ((OverdraftBankAccount) bank.account).setOverdraftLimit(overdraftInt);
                return 0; // Overdraft changed successfully
            } else {
                return 3; // This account does not have an overdraft facility
            }
        } else {
            return 1; // Overdraft must be a positive number
        }
    }

    // This method is used to process a withdrawal.
    // If the user is logged in or depositing, it prompts the user to enter a
    // withdrawal amount and sets the state to WITHDRAWING.
    // If the user is not logged in, it informs the user and resets the state.
    public void processWithdraw() {
        Debug.trace("Model::processWithdraw");
        if (state.equals(LOGGED_IN)) {
            number = 0;
            display1 = "Enter withdraw amount";
            display2 = "Your current balance is : " + formatBalance(bank.getBalance());
            if (bank.account instanceof OverdraftBankAccount) {
                display2 += "\nYour overdraft limit is: £" + ((OverdraftBankAccount) bank.account).getOverdraftLimit();
            }
            if (bank.account instanceof LimitedWithdrawalBankAccount) {
                int withdrawalsLeft = ((LimitedWithdrawalBankAccount) bank.account).getWithdrawalsLeft();
                if (withdrawalsLeft > 1) {
                    display2 += "\nYou have " + withdrawalsLeft + " withdrawals left.";
                } else if (withdrawalsLeft == 1) {
                    display2 += "\nYou have 1 withdrawal left.";
                } else {
                    // If the user has no withdrawals left, inform them and reset the state. exit method early via return
                    initialise("You have no withdrawals left. You cannot withdraw.");
                    return;
                }
            }
            setState(WITHDRAWING);
        } else if (!state.equals(LOGGED_IN)) {
            display2 += "\nYou're already in a transaction. Please either finish or cancel it before starting a new one.";
        } else {
            initialise("An error has occured, and the ATM has been reset.");
        }
        display(); // update the GUI
    }

    // This method is used to make a withdrawal.
    // It attempts to withdraw the specified amount from the bank.
    // If the withdrawal is successful, it informs the user and updates their balance.
    // If the withdrawal fails, it checks the type of account and informs the user accordingly.
    public void makeWithDrawal() {
        Debug.trace("Model::makeWithdrawal");
        // Define the amount to withdraw
        int withdrawalAmount = number;
        // Create a StringBuilder to build the message to display
        StringBuilder display2Builder = new StringBuilder();

        // Try to withdraw the amount from the bank
        if (bank.withdraw(withdrawalAmount)) {
            // If the withdrawal is successful, append the withdrawn amount and new balanceto the message
            display2Builder.append("Withdrawn: £").append(withdrawalAmount).append("\nYour new balance is now: ")
                    .append(formatBalance(bank.getBalance()));
            // If the account is an OverdraftBankAccount, append the overdraft limit to the message
            if (bank.account instanceof OverdraftBankAccount) {
                display2Builder.append("\nYour overdraft limit is: £")
                        .append(((OverdraftBankAccount) bank.account).getOverdraftLimit());
            }
        } else {
            // If the withdrawal is not successful, check the type of the account
            if (bank.account instanceof OverdraftBankAccount) {
                // If the account is an OverdraftBankAccount, calculate the available funds
                int availableFunds = bank.getBalance()
                        + Math.abs(((OverdraftBankAccount) bank.account).getOverdraftLimit());
                // Calculate the missing funds
                missing_funds = Math.max(0, withdrawalAmount - availableFunds);
                // If there are missing funds, append the required additional amount to the message
                if (missing_funds != 0) {
                    display2Builder.append("You do not have sufficient funds. You need an additional: £")
                            .append(missing_funds);
                }
            } else if (bank.account instanceof LimitedWithdrawalBankAccount) {
                // If the account is a LimitedWithdrawalBankAccount, check the number of withdrawals today
                if (((LimitedWithdrawalBankAccount) bank.account).getWithdrawalsLeft() <= 0) {
                    // If the maximum number of withdrawals for the day has been reached, append a message to that effect
                    display2Builder.append("You have reached your maximum withdrawals for the day.");
                } else {
                    // If the maximum number of withdrawals for the day has not been reached, try to withdraw the amount
                    if (bank.account.withdraw(withdrawalAmount)) {
                        // If the withdrawal is successful, append the withdrawn amount and new balance to the message
                        display2Builder.append("Withdrawn: £").append(withdrawalAmount)
                                .append("\nYour new balance is now: £").append(bank.getBalance());
                    } else {
                        // If the withdrawal is not successful, append the required additional amount to the message
                        display2Builder.append("You do not have sufficient funds, you require another £")
                                .append(Math.abs(bank.getBalance() - withdrawalAmount));
                    }
                }
            } else {
                // If the account is not an OverdraftBankAccount or a
                // LimitedWithdrawalBankAccount, calculate the missing funds
                missing_funds = Math.abs(bank.getBalance() - withdrawalAmount);
                // Append the required additional amount to the message
                display2Builder.append("You do not have sufficient funds, you require another: £")
                        .append(missing_funds);
            }
        }

        // Set the state to LOGGED_IN, clear display1, and set display2 to the built message.
        setState(LOGGED_IN);
        display1 = "";
        display2 = display2Builder.toString();
        // Reset the number to 0
        number = 0;
    }

    public void processDeposit() {
        Debug.trace("Model::processDeposit");
        if (state.equals(LOGGED_IN)) {
            // Switch to depositing state
            number = 0;
            setState(DEPOSITING);
            display1 = "Enter deposit amount";
            display2 = "Your current balance is : " + formatBalance(bank.getBalance());
        } else if (!state.equals(LOGGED_IN)) {
            // User is in a transaction
            display2 += "\nYou're already in a transaction. Please either finish or cancel it before starting a new one.";
        } else {
            // Unexpected state
            initialise("An error has occured, and the ATM has been reset.");
        }
        display(); // Update the GUI
    }

    public void makeDeposit() {
        Debug.trace("Model::makeDeposit");
        if (state.equals(DEPOSITING)) {
            if (number > 0) {
                if (bank.deposit(number)) {
                    // Deposit successful
                    setState(LOGGED_IN);
                    display1 = "";
                    display2 = "Deposit successful\n£" + number + " has been deposited to your Current Account\n"
                            + "Your new balance is now: " + formatBalance(bank.getBalance());
                } else {
                    // Deposit failed
                    initialise("Deposit failed. Please try again.");
                }
            } else {
                // Invalid deposit amount
                initialise("Invalid deposit amount. Please try again.");
            }
        }
        number = 0;
        display(); // Update the GUI
    }

    public void processBalance() {
        Debug.trace("Model::processBalance");
        if (state.equals(LOGGED_IN)) {
            display1 = "";
            display2 = "Your balance is: " + formatBalance(bank.getBalance());
            number = 0;
        } else if (!state.equals(LOGGED_IN)) {
            // User is in a transaction
            display2 += "\nYou're already in a transaction. Please either finish or cancel it before starting a new one.";
        } else {
            // Unexpected state
            initialise("An error has occured, and the ATM has been reset.");
        }
        display(); // Update the GUI
    }

    public void processTransfer() {
        Debug.trace("Model::processTransfer");
        if (state.equals(LOGGED_IN)) {
            // Switch to enteringAccount state
            targetAccountNumber = -1;
            number = 0;
            setState(ENTERING_ACCOUNT);
            display1 = "Enter target account";
            display2 = "Please enter the target account number above\nYour current balance is : "
                    + formatBalance(bank.getBalance());
        } else if (!state.equals(LOGGED_IN)) {
            // User is in a transaction
            display2 += "\nYou're already in a transaction. Please either finish or cancel it before starting a new one.";
        } else {
            // Unexpected state
            initialise("An error has occured, and the ATM has been reset.");
        }
        display(); // Update the GUI
    }

    public void makeTransfer() {
        Debug.trace("Model::makeTransfer");
        if (state.equals(ENTERING_ACCOUNT)) {
            // User has entered the target account number
            number = 0;
            setState(TRANSFERRING);
            display1 = "Enter transfer amount";
            display2 = "Your current balance is : " + formatBalance(bank.getBalance());
        } else if (state.equals(TRANSFERRING)) {
            // User has entered the transfer amount
            if (bank.transfer(accNumber, targetAccountNumber, number)) {
                // Transfer successful
                setState(LOGGED_IN);
                display1 = "";
                display2 = "Transfer successful\n£" + number + " has been transferred to account No."
                        + targetAccountNumber + "\n" + "Your new balance is now: " + formatBalance(bank.getBalance());
                targetAccountNumber = -1;
            } else {
                // Transfer failed
                initialise("Transfer failed. Please try again.");
            }
        } else {
            // User not logged in or in wrong state
            initialise("An error occured, and the ATM has been reset.");
        }
        number = 0;
        display(); // Update the GUI - 1
    }

    public static String formatBalance(int balance) {
        StringBuilder balanceBuilder = new StringBuilder();
        if (balance < 0) {
            balanceBuilder.append("-£").append(Math.abs(balance)).append("\nYou are in your overdraft");
        } else {
            balanceBuilder.append("£").append(balance);
        }
        return balanceBuilder.toString();
    }

    public void processStatement() {
        Debug.trace("Model::processStatement");
        if (state.equals(LOGGED_IN)) {
            number = 0;
            display1 = "Statement printed.";
            display2 = bank.getStatement();
        } else if (!state.equals(LOGGED_IN)) {
            // User is in a transaction
            display2 += "\nYou're already in a transaction. Please either finish or cancel it before starting a new one.";
        } else {
            // Unexpected state
            initialise("An error has occured, and the ATM has been reset.");
        }
        display(); // Update the GUI
    }

    // Any other key results in an error message and a reset of the GUI
    public void processUnknownKey(String action) {
        // unknown button, or invalid for this state - reset everything
        Debug.trace("Model::processUnknownKey: unknown button \"" + action + "\", re-initialising");
        // go back to initial state
        initialise("An error has occured, and the ATM has been reset.");
        display();
    }

    public void processCancel() {
        // Cancel button - reset everything
        Debug.trace("Model::processCancel");
        // go back to initial state
        initialise("Transaction cancelled");
    }

    public void processLogout() {
        // Logout button - reset everything
        Debug.trace("Model::processLogout");
        setState(LOGGED_OUT);
        // Ensure the user is logged out of the bank to prevent any potential exploits.
        bank.logout();
    }

    public void display() {
        Debug.trace("Model::display");
        controller.update(display1, display2);
    }
}