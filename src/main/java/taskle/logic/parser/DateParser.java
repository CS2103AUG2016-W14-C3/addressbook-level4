package taskle.logic.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

/**
 * Parser class for handling date and time using the
 * Natty library.
 * @author Abel
 *
 */
public class DateParser {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DateParser() {
    }

    /**
     * Parses given date and time string and returns 
     * an array of date time that we are interested in capturing.
     * Usually start and end dates or just deadline date.
     * @param dateTimeString String containing date and time to be parsed.
     * @return Array of dateTime found in String.
     */
    public static List<Date> parse(String dateTimeString) {
        assert dateTimeString != null && !dateTimeString.isEmpty();
        Parser parser = new Parser(TimeZone.getDefault());
        List<DateGroup> groups = parser.parse(dateTimeString);
        if (groups.isEmpty() || groups.get(0) == null) {
            return new ArrayList<>();
        }
        
        // We are only interested in the first date group
        DateGroup group = groups.get(0);
        List<Date> dates = group.getDates();
        
        // If time is inferred and not explicitly stated by user
        // We reset time because it would produce the current time
        boolean isTimeInferred = group.isTimeInferred();
        if (isTimeInferred) {
            resetTime(dates);
        }
        
        return dates;
    }
    
    /**
     * Resets the time fields in the date object to zeroes.
     * @param dates
     */
    private static void resetTime(List<Date> dates) {
        if (dates == null) {
            return;
        }
        for (Date date: dates) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            date.setTime(calendar.getTime().getTime());
        }
    }
}
