package CI401.mybank;

/**
 * This class represents the Model in the Model-View-Controller pattern.
 * It is responsible for managing the data and the business logic of the
 * application.
 * The Model communicates with the Bank object to perform banking operations.
 * The Model also communicates with the Controller and the View to update the
 * user interface.
 */

public class Model {

    // The Controller instance associated with this Model.
    public Controller controller;
    // The View instance associated with this Model.
    public View view;

    /**
     * Sets the controller for this model.
     * 
     * @param controller The controller to be set.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Sets the view for this model.
     * 
     * @param view The view to be set.
     */
    public void setView(View view) {
        this.view = view;
    }

    // Constants representing different states of the ATM.
    final String LOGGED_IN = "logged_in";
    final String CHANGE_PASSWORD = "change_password";
    final String WITHDRAWING = "withdrawing";
    final String DEPOSITING = "depositing";
    final String LOGGED_OUT = "logged_out";
    final String TRANSFERRING = "transferring";
    final String ENTERING_ACCOUNT = "enteringAccount";

    // Constants representing different status codes for password change and
    // overdraft
    public static final int PASSWORD_UPDATED = 0;
    public static final int PASSWORD_INCORRECT = 1;
    public static final int PASSWORD_MISMATCH = 2;
    public static final int PASSWORD_SAME = 3;

    public static final int OVERDRAFT_UPDATED = 0;
    public static final int OVERDRAFT_INVALID = 1;
    public static final int OVERDRAFT_EXCEEDS_BALANCE = 2;
    public static final int OVERDRAFT_NO_FACILITY = 3;
    public static final int OVERDRAFT_EXCEEDS_LIMIT = 4;

    // variables representing the ATM model
    String state = LOGGED_IN; // The current state of the ATM.
    int number = 0; // The current number displayed in the GUI.
    Bank bank = null; // The Bank object that the ATM communicates with.
    int targetAccountNumber = -1; // The target account number for a transfer operation.
    int accNumber = -1; // The account number entered by the user.
    String accPasswd = ""; // The password entered by the user.
    String newPassword = ""; // The new password entered by the user during password change.
    int missing_funds = 0; // The amount of funds missing during a withdrawal operation.
    String display1 = null; // The contents of the first message box in the GUI.
    String display2 = null; // The contents of the second message box in the GUI.

    /**
     * Constructor for the Model class.
     * 
     * @param b The bank to be associated with this model.
     */
    public Model(Bank b) {
        Debug.trace("Model::<constructor>");
        bank = b;
    }

    /**
     * Sets the account number and password for this model.
     * 
     * @param accNumber The account number to be set.
     * @param accPasswd The account password to be set.
     */
    public void setAccount(int accNumber, String accPasswd) {
        this.accNumber = accNumber;
        this.accPasswd = accPasswd;
    }

    /**
     * Gets the current account associated with this model.
     * 
     * @return The current account.
     */
    public BankAccount getCurrentAccount() {
        return bank.account;
    }

    /**
     * Initializes the ATM with a given message.
     * 
     * @param message The message to be displayed upon initialization.
     *                This message is displayed in the TextArea of the GUI.
     */
    public void initialise(String message) {
        Debug.trace("model::initialise");
        setState(LOGGED_IN);
        number = 0;

        display1 = "";
        display2 = message + "\nPlease choose from one of the six options.";
        display();
    }

    /**
     * Changes the state of the ATM.
     * 
     * @param newState The new state to be set.
     */
    public void setState(String newState) {
        if (!state.equals(newState)) {
            String oldState = state;
            state = newState;
            Debug.trace("Model::setState: changed state from " + oldState + " to " + newState);
        }
    }

    /**
     * Processes a number input. Also updates the display with the corresponding
     * number.
     * Also handles potential integer overflow problems and the "00" button.
     * 
     * @param label The number to be processed.
     *              This number is displayed in the TextField of the GUI.
     */
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

    /**
     * Processes the Clear button - reset the number (and number display string)
     */
    public void processClear() {
        // clear the number stored in the model
        number = 0;
        display1 = "";
        display(); // update the GUI
    }

    /**
     * Processes the Enter button - what we do depends what state the ATM is already
     * in
     */
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
                // I just put this here to ensure the user gets logged out if the program
                // glitches
                // And they somehow manage to press enter after clicking the logout button
                controller.process("LOGOUT");
                break;
            case LOGGED_IN:
            default:
                // do nothing in any other state (ie logged in)
        }
        display(); // update the GUI
    }

    /**
     * Changes the password of the account.
     * 
     * @param currentPassword The current password of the account.
     * @param newPassword     The new password to be set.
     * @param confirmPassword The confirmation of the new password.
     * @return The status of the password change operation.
     */
    public int changePassword(String currentPassword, String newPassword, String confirmPassword) {
        Debug.trace("Model::changePassword %s \n%s \n%s", currentPassword, newPassword, confirmPassword);
        // Check if the current password is correct
        if (!currentPassword.equals(accPasswd)) {
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
        accPasswd = newPassword; // Update the password in the model
        bank.updatePassword(accNumber, newPassword); // Update the password in the bank
        return PASSWORD_UPDATED; // Password updated successfully
    }

    /**
     * Changes the overdraft limit of the bank account.
     *
     * @param overdraft The new overdraft limit as a string.
     * @return An integer representing the result of the operation.
     */
    public int changeOverdraft(String overdraft) {
        Debug.trace("Model::changeOverdraft");

        // Using Regex to check if the overdraft string is a positive integer
        if (overdraft.matches("^[0-9]+$")) {
            // Convert the overdraft string to an integer
            int overdraftInt = Integer.parseInt(overdraft);

            // Check if the overdraft is more than 1000
            if (overdraftInt > 1000) {
                return OVERDRAFT_EXCEEDS_LIMIT;
            }
            // If the account balance is negative (i.e., in overdraft) and the new overdraft limit
            // is less than or equal to the absolute value of the balance, return an error code
            if (bank.getBalance() < 0 && overdraftInt <= Math.abs(bank.getBalance())) {
                return OVERDRAFT_EXCEEDS_BALANCE;
            }

            // If the account has an overdraft facility, set the new overdraft limit and
            // return a success code
            if (bank.account instanceof OverdraftBankAccount) {
                ((OverdraftBankAccount) bank.account).setOverdraftLimit(overdraftInt);
                return OVERDRAFT_UPDATED;
            } else {
                // If the account does not have an overdraft facility, return an error code
                return OVERDRAFT_NO_FACILITY;
            }
        } else {
            // If the overdraft string is not a positive integer, return an error code
            return OVERDRAFT_INVALID;
        }
    }

    /**
     * This method is used to process a withdrawal.
     * If the user is logged in or depositing, it prompts the user to enter a
     * withdrawal amount and sets the state to WITHDRAWING.
     * If the user is not logged in, it informs the user and resets the state.
     */
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
                    // If the user has no withdrawals left, inform them and reset the state. exit
                    // method early via return
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

    /**
     * This method is used to make a withdrawal.
     * It attempts to withdraw the specified amount from the bank.
     * If the withdrawal is successful, it informs the user and updates their
     * balance.
     * If the withdrawal fails, it checks the type of account and informs the user
     * accordingly.
     */
    public void makeWithDrawal() {
        Debug.trace("Model::makeWithdrawal");
        // Define the amount to withdraw
        int withdrawalAmount = number;
        // Create a StringBuilder to build the message to display
        StringBuilder display2Builder = new StringBuilder();

        // Try to withdraw the amount from the bank
        if (bank.withdraw(withdrawalAmount)) {
            // If the withdrawal is successful, append the withdrawn amount and new
            // balanceto the message
            display2Builder.append("Withdrawn: £").append(withdrawalAmount).append("\nYour new balance is now: ")
                    .append(formatBalance(bank.getBalance()));
            // If the account is an OverdraftBankAccount, append the overdraft limit to the
            // message
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
                // If there are missing funds, append the required additional amount to the
                // message
                if (missing_funds != 0) {
                    display2Builder.append("You do not have sufficient funds. You need an additional: £")
                            .append(missing_funds);
                }
            } else if (bank.account instanceof LimitedWithdrawalBankAccount) {
                // If the account is a LimitedWithdrawalBankAccount, check the number of
                // withdrawals today
                if (((LimitedWithdrawalBankAccount) bank.account).getWithdrawalsLeft() <= 0) {
                    // If the maximum number of withdrawals for the day has been reached, append a
                    // message to that effect
                    display2Builder.append("You have reached your maximum withdrawals for the day.");
                } else {
                    // If the maximum number of withdrawals for the day has not been reached, try to
                    // withdraw the amount
                    if (bank.account.withdraw(withdrawalAmount)) {
                        // If the withdrawal is successful, append the withdrawn amount and new balance
                        // to the message
                        display2Builder.append("Withdrawn: £").append(withdrawalAmount)
                                .append("\nYour new balance is now: £").append(bank.getBalance());
                    } else {
                        // If the withdrawal is not successful, append the required additional amount to
                        // the message
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

        // Set the state to LOGGED_IN, clear display1, and set display2 to the built
        // message.
        setState(LOGGED_IN);
        display1 = "";
        display2 = display2Builder.toString();
        // Reset the number to 0
        number = 0;
    }

    /**
     * This method is used to process a deposit.
     * If the user is logged in, it prompts the user to enter a deposit amount and
     * sets the state to DEPOSITING.
     * If the user is not logged in, it informs the user and resets the state.
     */
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

    /**
     * This method is used to make a deposit.
     * It attempts to deposit the specified amount into the bank.
     * If the deposit is successful, it informs the user and updates their balance.
     * If the deposit fails, it informs the user and resets the state.
     */
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

    /**
     * This method is used to process a balance check.
     * If the user is logged in, it displays the user's balance.
     * If the user is not logged in, it informs the user and resets the state.
     */
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

    /**
     * This method is used to process a transfer.
     * If the user is logged in, it prompts the user to enter the target account and
     * sets the state to ENTERING_ACCOUNT.
     * If the user is not logged in, it informs the user and resets the state.
     */
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

    /**
     * This method is used to make a transfer.
     * It attempts to transfer the specified amount to the target account.
     * If the transfer is successful, it informs the user and updates their balance.
     * If the transfer fails, it informs the user and resets the state.
     */
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
                initialise("Transfer failed. Please try again. \nMake sure the target account number is correct, and that there are enough funds in the account.\n");
            }
        } else {
            // User not logged in or in wrong state
            initialise("An error occured, and the ATM has been reset.");
        }
        number = 0;
        display(); // Update the GUI - 1
    }

    /**
     * This method is used to format the balance.
     * It formats the balance as a string with a currency symbol.
     * If the balance is negative, it adds a message about overdraft.
     *
     * @param balance The balance to be formatted, represented as an integer.
     * @return A string representation of the balance, formatted with a currency
     *         symbol and potentially an overdraft message.
     */
    public static String formatBalance(int balance) {
        StringBuilder balanceBuilder = new StringBuilder();
        if (balance < 0) {
            balanceBuilder.append("-£").append(Math.abs(balance)).append("\nYou are in your overdraft");
        } else {
            balanceBuilder.append("£").append(balance);
        }
        return balanceBuilder.toString();
    }

    /**
     * This method is used to process a statement request.
     * If the user is logged in, it displays the user's statement.
     * If the user is not logged in, it informs the user and resets the state.
     */
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

    /**
     * This method is used to process an unknown key.
     * It resets the state and informs the user about the error.
     *
     * @param action The action that was not recognized, represented as a string.
     */
    public void processUnknownKey(String action) {
        // unknown button, or invalid for this state - reset everything
        Debug.trace("Model::processUnknownKey: unknown button \"" + action + "\", re-initialising");
        // go back to initial state
        initialise("An error has occured, and the ATM has been reset.");
        display();
    }

    /**
     * This method is used to process a cancel request.
     * It resets the state and informs the user that the transaction has been
     * cancelled.
     */
    public void processCancel() {
        // Cancel button - reset everything
        Debug.trace("Model::processCancel");
        // go back to initial state
        initialise("Transaction cancelled");
    }

    /**
     * This method is used to process a logout request.
     * It logs the user out and resets the state.
     */
    public void processLogout() {
        // Logout button - reset everything
        Debug.trace("Model::processLogout");
        setState(LOGGED_OUT);
        // Ensure the user is logged out of the bank to prevent any potential exploits.
        bank.logout();
    }

    /**
     * This method is used to update the display.
     * It updates the display with the current state information.
     */
    public void display() {
        Debug.trace("Model::display");
        controller.update(display1, display2);
    }
}