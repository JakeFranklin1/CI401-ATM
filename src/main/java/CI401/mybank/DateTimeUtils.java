package CI401.mybank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The DateTimeUtils class provides methods for getting the current date and time.
 */
public class DateTimeUtils {

    /**
     * Returns the current date and time as an array of strings.
     *
     * @return an array of strings containing the current date and time
     */
    public static String[] getCurrentDateTime() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy"); // Create a formatter for the date
        String date = now.format(dateFormatter); // Format the current date

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = now.format(timeFormatter);

        return new String[] { date, time };
    }
}
