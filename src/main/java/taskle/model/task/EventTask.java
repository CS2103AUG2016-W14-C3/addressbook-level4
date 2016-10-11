package taskle.model.task;

import java.util.Date;

import taskle.commons.util.DateFormatUtil;
import taskle.model.tag.UniqueTagList;

/**
 * Event task object that guarantees non-null fields for task
 * and nullable fields for event start and end dates.
 * @author Abel
 *
 */
public class EventTask extends Task {
        
    private Date startDate;
    private Date endDate;

    public EventTask(Name name, Date startDateTime, Date endDateTime, UniqueTagList tags) {
        super(name, tags);
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

    public EventTask(ModifiableTask source) {
        super(source);
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

    @Override
    public Task copy() {
        return new EventTask((ReadOnlyTask) this);
    }

}
