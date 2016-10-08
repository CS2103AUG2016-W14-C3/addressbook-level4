package taskle.model.task;

import taskle.model.tag.UniqueTagList;

/**
 * A Task with no other details like time and dates.
 * It only guarantees a task name.
 * @author Abel
 *
 */
public class FloatTask extends Task {

    public FloatTask(Name name, UniqueTagList tags) {
        super(name, tags);
    }
    
    /**
     * Copy constructor.
     */
    public FloatTask(ReadOnlyTask source) {
        super(source);
    }
    
    /**
     * Copy constructor.
     */
    public FloatTask(ModifiableTask source) {
        super(source);
    }
    
    @Override
    public String getDetailsString() {
        return "";
    }

}