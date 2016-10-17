package taskle.model.task;

import java.util.Comparator;

/**
 * Custom Comparator that compares tasks when being sorted in internalList
 * @author Muhammad Hamsyari
 *
 */
public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task mainTask, Task taskToCompare) {
        assert mainTask != null && taskToCompare != null;
        
        /**
         * Compare by Type followed by Name/Date
         * Priority of Type: EventTask/DeadlineTask > FloatTask
         */
        if (mainTask instanceof FloatTask && taskToCompare instanceof FloatTask) {
            return mainTask.getName().fullName.compareTo(taskToCompare.getName().fullName);
        } else if (mainTask instanceof FloatTask) {
            return 1;
        } else if (taskToCompare instanceof FloatTask) {
            return -1;
        } else if (mainTask instanceof DeadlineTask) {
            if (taskToCompare instanceof DeadlineTask) {
                return ((DeadlineTask) mainTask).getDeadlineDate().compareTo(((DeadlineTask) taskToCompare).getDeadlineDate());
            } else {
                return ((DeadlineTask) mainTask).getDeadlineDate().compareTo(((EventTask) taskToCompare).getStartDate());
            }
        } else {
           if (taskToCompare instanceof EventTask) {
               return ((EventTask) mainTask).getStartDate().compareTo(((EventTask) taskToCompare).getStartDate());
           } else {
               return ((EventTask) mainTask).getStartDate().compareTo(((DeadlineTask) taskToCompare).getDeadlineDate());
           }
        }
    }
}
