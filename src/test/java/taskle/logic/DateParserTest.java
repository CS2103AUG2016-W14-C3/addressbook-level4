package taskle.logic;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import taskle.logic.parser.DateParser;

public class DateParserTest {
    
    private Calendar calendar = Calendar.getInstance();

    @Before
    public void setup() {
        calendar.clear();
    }
    
    @Test
    public void parseDate_singleDate_returnDateListWithOneDate() {
        String singleDateString = "14 Feb 2016";
        List<Date> expected =  new ArrayList<>();
        calendar.set(2016, 1, 14);
        Date singleDateExpected = calendar.getTime();
        expected.add(singleDateExpected);
        List<Date> actual = DateParser.parse(singleDateString);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
    
    @Test
    public void parseDate_twoDates_returnDateListWithTwpDates() {
        String singleDateString = "14 Feb 2016 to 16 Feb 2016";
        List<Date> expected =  new ArrayList<>();
        calendar.set(2016, 1, 14);
        Date firstDateExpected = calendar.getTime();
        expected.add(firstDateExpected);
        calendar.set(2016, 1, 16);
        Date secondDateExpected = calendar.getTime();
        expected.add(secondDateExpected);
        List<Date> actual = DateParser.parse(singleDateString);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
    
    @Test
    public void parseDate_noDates_returnEmptyDateList() {
        String singleDateString = "I'm a latecomer who always goes to school on time";
        List<Date> expected =  new ArrayList<>();
        List<Date> actual = DateParser.parse(singleDateString);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
    
    @Test(expected= AssertionError.class)
    public void parseDate_nullInput_throwsAssertionError() {
        String singleDateString = null;
        List<Date> expected =  new ArrayList<>();
        List<Date> actual = DateParser.parse(singleDateString);
    }
    
    @Test(expected= AssertionError.class)
    public void parseDate_emptyString_throwsAssertionError() {
        String singleDateString = "";
        List<Date> expected =  new ArrayList<>();
        List<Date> actual = DateParser.parse(singleDateString);
    }
    
}
