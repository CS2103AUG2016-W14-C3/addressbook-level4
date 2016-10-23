package taskle.model.task;

import java.util.Date;
import java.util.Objects;

import taskle.commons.util.CollectionUtil;
import taskle.commons.util.DateFormatUtil;
import taskle.model.tag.UniqueTagList;

/**
 * Abstraction for all Task in the task manager.
 * Guarantees: details are present and not null, field values are validated.
 */
public abstract class Task implements ReadOnlyTask {

    protected Name name;
    protected boolean isTaskDone;
    protected Date remindDate;
    
    protected UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, tags);
        this.name = name;
        this.remindDate = null;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }
    
    public Task(Name name, Date remindDate, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, tags);
        this.name = name;
        this.remindDate = remindDate;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getRemindDate(), source.getTags());
        setTaskDone(source.isTaskDone());
    }

    
    @Override
    public Name getName() {
        return name;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }
    
    @Override
    public boolean isTaskDone() {
        return isTaskDone;
    }

    public void setName(Name name) {
        this.name = name;
    }
    
    public void setTaskDone(boolean taskDone) {
        this.isTaskDone = taskDone;
    }
    
    @Override
    public Date getRemindDate() {
        return remindDate;
    }
    
    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    /**
     * Replaces this task's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, tags);
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
