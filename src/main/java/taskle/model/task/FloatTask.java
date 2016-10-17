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
     * Method to return a float task from the given deadline task
     * @param source
     * @return DeadlineTask
     */
    public DeadlineTask changeToDeadlineTask(FloatTask source) {
        assert source != null;
        return new DeadlineTask(source);
    }
    
    /**
     * Method to return a float task from the given event task
     * @param source
     * @return
     */
    public EventTask changeToEventTask (FloatTask source) {
        assert source != null;
        return new EventTask(source);
    }
    @Override
    public String getDetailsString() {
        return "";
    }

    @Override
    public Task copy() {
        return new FloatTask((ReadOnlyTask) this);
    }
    
}