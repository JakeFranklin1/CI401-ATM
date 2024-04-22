package CI401.mybank;

/**
 * The LimitedWithdrawalBankAccount class represents a bank account with a limit
 * on the number of withdrawals per day.
 * It extends the BankAccount class and overrides the withdraw method to enforce
 * the limit.
 */

public class LimitedWithdrawalBankAccount extends BankAccount {
    /**
     * The maximum number of withdrawals allowed per day.
     */

    protected static final int MAX_WITHDRAWALS_PER_DAY = 3;

    /**
     * The number of withdrawals made today.
     */

    protected int withdrawalsToday;

    /**
     * Constructor for the LimitedWithdrawalBankAccount class.
     * It initializes the account with the given account number, password, and
     * balance,
     * and sets the account type to "limited".
     *
     * @param accNumber The account number.
     * @param accPasswd The password.
     * @param balance   The balance.
     */

    public LimitedWithdrawalBankAccount(int accNumber, String accPasswd, int balance) {
        super(accNumber, accPasswd, balance); // Call the constructor of the superclass (BankAccount)
        this.withdrawalsToday = 0; // Initialize the number of withdrawals made today to 0
        this.accountType = "limited"; // Set the account type to "limited"
    }

    /**
     * Returns the number of withdrawals left for the day.
     *
     * @return The number of withdrawals left for the day.
     */

    public int getWithdrawalsLeft() {
        return MAX_WITHDRAWALS_PER_DAY - withdrawalsToday;
    }

    /**
     * Withdraws a certain amount of money from the account.
     * The withdrawal is only successful if the amount is positive,
     * the number of withdrawals made today is less than the maximum allowed,
     * and the balance is greater than or equal to the amount.
     *
     * @param amount The amount of money to be withdrawn.
     * @return true if the withdrawal was successful, false otherwise.
     */

    @Override
    public boolean withdraw(int amount) {
        Debug.trace("LimitedWithdrawalBankAccount::withdraw: amount =" + amount);

        if (amount < 0 || withdrawalsToday >= MAX_WITHDRAWALS_PER_DAY || balance < amount) {
            return false;
        } else {
            balance -= amount; // subtract amount from balance
            withdrawalsToday++;
            System.out.println("\n" + withdrawalsToday + "\n");
            return true;
        }
    }
}