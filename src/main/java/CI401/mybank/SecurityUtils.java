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
     * Checks if the given password matches the hashed password using OpenBSDBCrypt
     * algorithm.
     *
     * @param hashedPassword the hashed password to be checked against
     * @param password       the password to be checked
     * @return true if the password matches the hashed password, false otherwise
     */
    public static boolean checkPassword(String hashedPassword, String password) {
        return OpenBSDBCrypt.checkPassword(hashedPassword, password.toCharArray());
    }
}