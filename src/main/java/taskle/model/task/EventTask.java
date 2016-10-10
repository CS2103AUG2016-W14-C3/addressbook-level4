package taskle.model.task;

import java.util.Date;

import taskle.model.tag.UniqueTagList;

public class EventTask extends Task {
    
    private static final String START_END_DELIMITER = "to";
    
    private Date startDateTime;
    private Date endDateTime;

    public EventTask(Name name, Date startDateTime, Date endDateTime, UniqueTagList tags) {
        super(name, tags);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public EventTask(ReadOnlyTask source) {
        super(source);
    }

    public EventTask(ModifiableTask source) {
        super(source);
    }

    @Override
    public String getDetailsString() {
        return startDateTime.toString() + START_END_DELIMITER
                + endDateTime.toString();
    }

}
