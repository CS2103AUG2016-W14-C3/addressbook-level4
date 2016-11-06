package taskle.model.task;

import java.util.Date;

/**
 * A Task with no other details like time and dates.
 * It only guarantees a task name.
 * @author Abel
 *
 */
public class FloatTask extends Task {

    public FloatTask(Name name) {
        super(name);
    }
    
    public FloatTask(Name name, Date remindDate) {
        super(name, remindDate);
    }
    
    // Copy constructor.
    public FloatTask(ReadOnlyTask source) {
        super(source);
    }
    
    @Override
    public String getDetailsString() {
        return "";
    }

    @Override
    public Task copy() {
        return new FloatTask(this);
    }

    @Override
    public Status getStatus() {
        if (isTaskDone) {
            return Status.DONE;
        }
        
        return Status.FLOAT;
    }
    
}
