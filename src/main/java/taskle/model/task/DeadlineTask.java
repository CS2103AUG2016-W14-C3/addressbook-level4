package taskle.model.task;

import java.util.Calendar;
import java.util.Date;

import taskle.commons.util.DateFormatUtil;

/**
 * Deadline task object that guarantees non-null fields for task and nullable
 * field for deadlineDate.
 * 
 * @author Abel
 *
 */
public class DeadlineTask extends Task {

    private Date deadlineDate;

    public DeadlineTask(Name name, Date deadlineDate) {
        super(name);
        this.deadlineDate = deadlineDate;
    }
    
    public DeadlineTask(Name name, Date deadlineDate, Date remindDate) {
        super(name, remindDate);
        this.deadlineDate = deadlineDate;
    }
    

    public DeadlineTask(ReadOnlyTask source) {
        super(source);
        
        if (source instanceof DeadlineTask) {
            deadlineDate = ((DeadlineTask) source).getDeadlineDate();
        }
    }

    @Override
    public String getDetailsString() {
        return DateFormatUtil.formatSingleDate(deadlineDate);
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }
        
    @Override
    public Task copy() {
        return new DeadlineTask(this);
    }

    /**
     * Converts the task into a string that can represent its addition in a
     * command as well.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(" by ");
        builder.append(getDetailsString());
        return builder.toString();
    }

    @Override
    public Status getStatus() {
        Calendar calendar = Calendar.getInstance();
        Date nowDate = calendar.getTime();
        if (isTaskDone) { 
            return Status.DONE;
        } else if (nowDate.before(deadlineDate)) {
            return Status.PENDING;
        } else {
            return Status.OVERDUE;
        }
    }

}
