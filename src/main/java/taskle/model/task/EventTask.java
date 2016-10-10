package taskle.model.task;

import java.util.Date;

import taskle.model.tag.UniqueTagList;
import taskle.model.task.ReadOnlyTask.TaskType;

public class EventTask extends Task {
    
    public static final TaskType EVENT_TASK_TYPE = TaskType.EVENT;
    private static final String START_END_DELIMITER = "to";
    
    private Date startDate;
    private Date endDate;

    public EventTask(Name name, Date startDateTime, Date endDateTime, UniqueTagList tags) {
        super(name, tags);
        this.startDate = startDateTime;
        this.endDate = endDateTime;
    }

    public EventTask(ReadOnlyTask source) {
        super(source);
    }

    public EventTask(ModifiableTask source) {
        super(source);
    }

    @Override
    public String getDetailsString() {
        return startDate.toString() + START_END_DELIMITER
                + endDate.toString();
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public Date getstartDate() {
        return startDate;
    }

    @Override
    public TaskType getTaskType() {
        return EVENT_TASK_TYPE;
    }

}
