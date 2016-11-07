package taskle.commons.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

//@@author A0139402M

public class TaskUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void changeDeadlineToEvent_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.deadlineChangeToEvent(null);
    }

    @Test
    public void changeDeadlineToFloat_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.deadlineChangeToFloat(null);
    }
    
    @Test
    public void changeFloatToEvent_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.floatChangeToEvent(null);
    }
    
    @Test
    public void changeFloatToDeadline_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.floatChangeToDeadline(null);
    }
    
    @Test
    public void changeEventToDeadline_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.eventChangeToDeadline(null);
    }
    
    @Test
    public void changeEventToFloat_nullGiven_assertionError(){
        thrown.expect(AssertionError.class);
        TaskUtil.eventChangeToFloat(null);
    }

}
