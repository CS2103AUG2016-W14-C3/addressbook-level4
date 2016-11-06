package taskle.commons.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

//@@author A0141780J
/**
 * JUnit tests for DateFormatUtil to ensure that we are formatting the date
 * according to our UI design.
 * @author Abel
 *
 */
public class DateFormatUtilTest {
    
    public Calendar calendar = Calendar.getInstance();
    
    @Before
    public void reset() {
        calendar.clear();
    }

    @Test
    public void formatDate_dateWithTime_returnDateCommaTime(){
        String expected = "10 Nov 2016, 7:00PM";
        calendar.set(2016, 10, 10, 19, 0);
        Date inputDate = calendar.getTime();
        String actual = DateFormatUtil.formatSingleDate(inputDate);
        assertEquals(expected, actual);
    }


    @Test
    public void formatDate_dateWithTime2359_returnDateOnly(){
        String expected = "1 Jan 2016";
        calendar.set(2016, 0, 1, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date inputDate = calendar.getTime();
        String actual = DateFormatUtil.formatSingleDate(inputDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDayDifferentTime_returnDateCommaTimeToTime(){
        String expected = "14 Feb 2016, 6:00PM to 6:30PM";
        calendar.set(2016, 1, 14, 18, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 1, 14, 18, 30);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDaySameTime_returnDateCommaTime(){
        String expected = "18 Apr 2016, 3:00PM";
        calendar.set(2016, 3, 18, 15, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 3, 18, 15, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_differentDaySameTime_returnFullDateTimeToDateTime(){
        String expected = "17 Oct 2016, 3:00PM to 18 Oct 2016, 3:00PM";
        calendar.set(2016, 9, 17, 15, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 18, 15, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDay12Am_return12AmSingleDate(){
        String expected = "17 Oct 2016, 12:00AM";
        calendar.set(2016, 9, 17, 00, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 17, 00, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_differentDaysNoTime_returnTwoDatesWithNoTime() {
        String expected = "17 Oct 2016 to 18 Oct 2016";
        calendar.set(2016, 9, 17, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 18, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_from2359Day1ToDay2_returnSingleDateToDateTime(){
        String expected = "17 Oct 2016 to 18 Oct 2016, 11:58PM";
        calendar.set(2016, 9, 17, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 18, 23, 58, 00);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatForAddCommand_from2359Day1ToDay2_returnSingleDateToDateTime(){
        String expected = "17 Oct 2016, 11:59PM to 18 Oct 2016, 11:58PM";
        calendar.set(2016, 9, 17, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 18, 23, 58, 00);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.getDateArgString(startDate, endDate);
        assertEquals(expected, actual);
    }
    
}
