package taskle.model.task;

import java.util.Date;

import taskle.model.tag.UniqueTagList;

public class DeadlineTask extends Task {
    
    private Date deadlineDate;

    public DeadlineTask(Name name, Date deadlineDate, UniqueTagList tags) {
        super(name, tags);
    }

    public DeadlineTask(ReadOnlyTask source) {
        super(source);
    }

    public DeadlineTask(ModifiableTask source) {
        super(source);
    }

    @Override
    public String getDetailsString() {
        return deadlineDate.toString();
    }

}
