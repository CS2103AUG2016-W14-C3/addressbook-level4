package taskle.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilities method for formatting Dates into Strings for display.
 * 
 * @author Abel
 */
public class DateFormatUtil {
    //@@author A0141780J
    /**
     * Patterns for formatting. One for just the date and another with the time
     */
    private static final String DATE_DISPLAY_PATTERN = "d MMM yyyy";
    private static final String TIME_DISPLAY_PATTERN = "h:mma";
    private static final String DATE_TIME_DISPLAY_PATTERN = "d MMM yyyy, h:mma";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_DISPLAY_PATTERN);
    private static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_DISPLAY_PATTERN);
    private static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_DISPLAY_PATTERN);

    private static final String EVENT_DATES_DELIMITER = " to ";

    private static final Calendar calendar = Calendar.getInstance();

    /** Private constructor to prevent instantiation. */
    private DateFormatUtil() {
    }

    /**
     * Formats given start and end dates according to their respective time and
     * dates in our desired format.
     * 
     * @param startDate start date object
     * @param endDate end date object
     * @return formatted string for date time for display to user
     */
    public static String formatEventDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return "";
        }

        calendar.setTime(startDate);
        int startDay = calendar.get(Calendar.DAY_OF_YEAR);
        int startYear = calendar.get(Calendar.YEAR);

        calendar.setTime(endDate);
        int endDay = calendar.get(Calendar.DAY_OF_YEAR);
        int endYear = calendar.get(Calendar.YEAR);

        if (startDate.equals(endDate)) {
            return formatSingleDate(endDate);
        } else if (startDay == endDay && startYear == endYear) {
            return formatSameDayEventDates(startDate, endDate);
        } else {
            return formatTwoDaysEventDates(startDate, endDate);
        }
    }
    
    /**
     * Formats the date as a String such that time is not shown if its 
     * 23:59:99.
     * Date is also displayed in format as designed in UI.
     * 
     * @param date Given date object to format
     * @return formatted date time string for display to user
     */
    public static String formatSingleDate(Date date) {
        if (date == null) {
            return "";
        }

        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        int msec = calendar.get(Calendar.MILLISECOND);
        if (hour == 23 && min == 59 && sec == 59) {
            return SIMPLE_DATE_FORMAT.format(date);
        } else {
            return SIMPLE_DATE_TIME_FORMAT.format(date);
        }
    }
    
    private static String formatSameDayEventDates(Date startDate, Date endDate) {
        return formatSingleDate(startDate) 
               + EVENT_DATES_DELIMITER
               + SIMPLE_TIME_FORMAT.format(endDate);
    }
    
    private static String formatTwoDaysEventDates(Date startDate, Date endDate) {
        return formatSingleDate(startDate) + EVENT_DATES_DELIMITER 
               + formatSingleDate(endDate);
    }

    //@@author A0139402M
    /**
     * Formats the reminder date given. If no time is specified, it'll default
     * to 00:00 timing for the date specified.
     * 
     * @param date
     * @return
     */
    public static String formatRemindDate(Date date) {
        if (date == null) {
            return "";
        }
        
        return SIMPLE_DATE_TIME_FORMAT.format(date);
    }

    //@@author A0141780J
    /**
     * Used for converting a given array of dates into an add command friendly
     * format.
     * 
     * @param dates dates to be added to this formatted String
     * @return command ready format for the dates
     */
    public static String getDateArgString(Date... dates) {
        String[] dateStrings = new String[dates.length];
        for (int i = 0; i < dates.length; i++) {
            dateStrings[i] = SIMPLE_DATE_TIME_FORMAT.format(dates[i]);
        }
        
        return String.join(EVENT_DATES_DELIMITER, dateStrings);
    }
}
