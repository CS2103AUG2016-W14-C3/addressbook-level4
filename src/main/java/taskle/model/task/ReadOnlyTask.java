package taskle.model.task;

import java.util.Date;

/**
 * A read-only immutable interface for a Task in the taskmanager.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyTask {
    
    public enum Status {
        FLOAT, PENDING, OVERDUE, DONE
    }
    
    Name getName();
    
    Date getRemindDate();

    // Returns true if both have the same state. (interfaces cannot override .equals)
    default boolean isSameStateAs(ReadOnlyTask other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName())); // state checks here onwards
    }
    
    public abstract Task copy();
    
    public Status getStatus();
    
    public String getDetailsString();

    public String getRemindDetailsString();
    
    //@@author A0125509H
    public boolean isTaskDone();
    //@@author

}
