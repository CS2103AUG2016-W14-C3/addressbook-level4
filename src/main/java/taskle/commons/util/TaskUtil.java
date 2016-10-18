package taskle.commons.util;

import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;

public class TaskUtil {
    
    /**
     * Method to return an EventTask from the given DeadlineTask
     * @param source
     * @return
     */
    public static EventTask deadlineChangeToEvent(DeadlineTask source) {
        assert source != null;
        assert source.getDeadlineDate() != null;
        return new EventTask(source);
    }
    
    /**
     * Method to return a FloatTAsk from the given DeadlineTask
     * @param source
     * @return
     */
    public static FloatTask deadlineChangeToFloat(DeadlineTask source) {
        assert source != null;
        return new FloatTask(source);
    }
    
    /**
     * Method to return a DeadlineTask from the given EventTask
     * @param source
     * @return DeadlineTask
     */
    public static DeadlineTask eventChangeToDeadline(EventTask source) {
        assert source != null;
        assert source.getStartDate() != null || source.getEndDate() != null;
        return new DeadlineTask(source);               
    }
    
    /**
     * Method to return a FloatTask from the given EventTask
     * @param source
     * @return
     */
    public static FloatTask eventChangeToFloat(EventTask source) {
        assert source != null;
        return new FloatTask(source);  
    }
    
    /**
     * Method to return a float task from the given deadline task
     * @param source
     * @return DeadlineTask
     */
    public static DeadlineTask floatChangeToDeadline(FloatTask source) {
        assert source != null;
        return new DeadlineTask(source);
    }
    
    /**
     * Method to return a float task from the given event task
     * @param source
     * @return
     */
    public static EventTask floatChangeToEvent (FloatTask source) {
        assert source != null;
        return new EventTask(source);
    }
    
}
