//package taskle.model.task;
//
//import java.util.Date;
//
//import taskle.commons.exceptions.IllegalValueException;
//
///**
// * Represents a Task's DateTime data in the address book. Guarantees: immutable;
// * is valid as declared in {@link #isValidTime(String)}
// */
//public class DateTime {
//
//    public static final String DATE_SPACING = "/";
//    public static final String TIME_SPACING = " at ";
//
//    // Date variables
//    private int day;
//    private int month;
//    private int year;
//    
//    // Time variables
//    private int hours;
//    private int mins;
//    private int seconds;
//
//    private int timeInt;
//
//    /**
//     * Validates given timeString.
//     *
//     * @throws IllegalValueException
//     *             if given name string is invalid.
//     */
//    public DateTime(Date date) {
//        assert date != null;
//        day = date.getDay();
//        month = date.getMonth();
//        year = date.getYear();
//        timeInt = date.get
//    }
//
//    @Override
//    public String toString() {
//        String string = String.valueOf(day) + DATE_SPACING 
//               + String.valueOf(month) + DATE_SPACING
//               + String.valueOf(year);
//        if (timeInt != 0) {
//            string += String.valueOf(timeInt);
//        }
//        return string;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        return other == this || 
//                (other instanceof DateTime && this.day == (((DateTime) other).day)
//                && this.month == (((DateTime) other).month) 
//                && this.year == (((DateTime) other).year));
//
//    }
//
//    @Override
//    public int hashCode() {
//        return timeInt;
//    }
//
//}
