package CI401.mybank;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

/**
 * The SecurityUtils class provides methods for hashing and checking passwords
 */
public class SecurityUtils {

    /**
     * Hashes the given password using OpenBSDBCrypt algorithm.
     * The hashed password is generated using a random salt and a cost of 12.
     * The salt and cost are stored in the hashed password.
     *
     * @param password the password to be hashed
     * @return the hashed password
     */

    public static String hashPassword(String password) {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        int cost = 12;
        return OpenBSDBCrypt.generate(password.toCharArray(), salt, cost);
    }

    /**
     * This method checks if the provided password matches the hashed password.
     *
     * @param hashedPassword The hashed password, typically retrieved from the database. 
     * This is the hashed version of the original password that was created when the user 
     * set or last changed their password.
     * @param password The plaintext password provided by the user trying to authenticate, 
     * such as during a login attempt.
     * @return Returns true if the provided password matches the hashed password, false otherwise. 
     * The method uses OpenBSDBCrypt.checkPassword(), which hashes the provided password in the same way 
     * as the original password was hashed, then compares the result to the stored hashed password. 
     * If they match, it means the correct password was provided.
     */
    public static boolean checkPassword(String hashedPassword, String password) {
        return OpenBSDBCrypt.checkPassword(hashedPassword, password.toCharArray());
    }
}