package taskle.model.task;

import java.util.Calendar;
import java.util.Date;

import taskle.commons.util.DateFormatUtil;

//@@author A0141780J
/**
 * Event task object that guarantees non-null fields for task
 * and nullable fields for event start and end dates.
 * @author Abel
 *
 */
public class EventTask extends Task {
        
    private Date startDate;
    private Date endDate;
    
    public EventTask(Name name, Date startDateTime, Date endDateTime) {
        super(name);
        this.startDate = startDateTime;
        this.endDate = endDateTime;
    }

    public EventTask(Name name, Date startDateTime, Date endDateTime, Date remindDate) {
        super(name, remindDate);
        this.startDate = startDateTime;
        this.endDate = endDateTime;
    }
    
    public EventTask(ReadOnlyTask source) {
        super(source);
        
        if (source instanceof EventTask) {
            EventTask event = (EventTask) source;
            startDate = event.getStartDate();
            endDate = event.getEndDate();
        }
    }

    @Override
    public String getDetailsString() {
        return DateFormatUtil.formatEventDates(startDate, endDate);
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public Date getStartDate() {
        return startDate;
    }
   
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
   
    @Override
    public Task copy() {
        return new EventTask(this);
    }
    
    /**
     * Converts the task into a string that can represent
     * its addition in a command as well.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(" from ");
        builder.append(getDetailsString());
        return builder.toString();
    }

    @Override
    public Status getStatus() {
        if (isTaskDone) {
            return Status.DONE;
        }
        
        Calendar calendar = Calendar.getInstance();
        Date nowDate = calendar.getTime();
        if (nowDate.before(startDate)) {
            return Status.PENDING;
        } else {
            return Status.OVERDUE;
        }
    }

}
