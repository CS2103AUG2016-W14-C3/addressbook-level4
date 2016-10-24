package taskle.commons.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

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
    public void formatDate_dateWithTime_returnTimeCommaDate(){
        String expected = "7:00PM, 10 Nov 2016";
        calendar.set(2016, 10, 10, 19, 0);
        Date inputDate = calendar.getTime();
        String actual = DateFormatUtil.formatDate(inputDate);
        assertEquals(expected, actual);
    }


    @Test
    public void formatDate_dateWithoutTime_returnDateOnly(){
        String expected = "1 Jan 2016";
        calendar.set(2016, 0, 1, 0, 0);
        Date inputDate = calendar.getTime();
        String actual = DateFormatUtil.formatDate(inputDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDayDifferentTime_returnTimeToTimeCommaDate(){
        String expected = "6:00PM to 6:30PM, 14 Feb 2016";
        calendar.set(2016, 1, 14, 18, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 1, 14, 18, 30);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDaySameTime_returnTimeCommaDate(){
        String expected = "3:00PM, 18 Apr 2016";
        calendar.set(2016, 3, 18, 15, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 3, 18, 15, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_differentDaySameTime_returnFullDateTimeToDateTime(){
        String expected = "3:00PM, 17 Oct 2016 to 3:00PM, 18 Oct 2016";
        calendar.set(2016, 9, 17, 15, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 18, 15, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }
    
    @Test
    public void formatEventDate_sameDay12Am_return(){
        String expected = "17 Oct 2016";
        calendar.set(2016, 9, 17, 00, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 17, 00, 0);
        Date endDate = calendar.getTime();
        String actual = DateFormatUtil.formatEventDates(startDate, endDate);
        assertEquals(expected, actual);
    }

}
