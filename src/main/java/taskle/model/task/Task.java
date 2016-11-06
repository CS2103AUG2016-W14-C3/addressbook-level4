package taskle.model.task;

import java.util.Date;
import java.util.Objects;

import taskle.commons.util.CollectionUtil;
import taskle.commons.util.DateFormatUtil;

/**
 * Abstraction for all Task in the task manager.
 * Guarantees: details are present and not null, field values are validated.
 */
public abstract class Task implements ReadOnlyTask {

    protected Name name;
    
    //@@author A0139402M
    protected Date remindDate;
    
    //@@author A0125509H
    protected boolean isTaskDone;
    
    //@@author

    //Every field must be present and not null.
    public Task(Name name) {
        assert !CollectionUtil.isAnyNull(name);
        this.name = name;
        this.remindDate = null;
    }
    
    public Task(Name name, Date remindDate) {
        assert !CollectionUtil.isAnyNull(name);
        this.name = name;
        this.remindDate = remindDate;
    }

    // Copy constructor.
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getRemindDate());
        setTaskDone(source.isTaskDone());
    }

    
    @Override
    public Name getName() {
        return name;
    }
    
    //@@author A0125509H
    @Override
    public boolean isTaskDone() {
        return isTaskDone;
    }
    //@@author

    public void setName(Name name) {
        this.name = name;
    }
    
    //@@author A0125509H
    public void setTaskDone(boolean taskDone) {
        this.isTaskDone = taskDone;
    }
    
    //@@author A0139402M
    @Override
    public Date getRemindDate() {
        return remindDate;
    }
    
    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }
    //@@author

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name);
    }

    /**
     * Converts the task into a string that can represent
     * its addition in a command as well.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        return builder.toString();
    }
    
    @Override
    public String getRemindDetailsString() {
        return DateFormatUtil.formatRemindDate(remindDate);
    }
}
