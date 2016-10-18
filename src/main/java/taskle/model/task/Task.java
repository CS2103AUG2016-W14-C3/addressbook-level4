package taskle.model.task;

import java.util.Objects;

import taskle.commons.util.CollectionUtil;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;

/**
 * Abstraction for all Task in the task manager.
 * Guarantees: details are present and not null, field values are validated.
 */
public abstract class Task implements ReadOnlyTask {

    protected Name name;
    protected boolean isTaskDone;

    protected UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, tags);
        this.name = name;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getTags());
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
}
