package taskle.model.task;

import java.util.Comparator;

import taskle.model.task.ReadOnlyTask.Status;

/**
 * Custom Comparator that compares tasks when being sorted in internalList
 * @author Muhammad Hamsyari
 *
 */
public class TaskComparator implements Comparator<Task> {
    
    private static final int PRIORITY_OVERDUE = 3;
    private static final int PRIORITY_PENDING = 2;
    private static final int PRIORITY_FLOAT = 1;
    private static final int PRIORITY_DONE = 0;

    @Override
    public int compare(Task mainTask, Task taskToCompare) {
        assert mainTask != null && taskToCompare != null;
        
        /**
         * Compare by Type followed by Name/Date
         * Priority of Type: Overdue > Pending > FloatTask > Done
         */
        Status mainStatus = mainTask.getStatus();
        Status otherStatus = taskToCompare.getStatus();
        int mainPriority = getPriority(mainStatus);
        int otherPriority = getPriority(otherStatus);
        
        if (mainPriority > otherPriority) {
            return -1;
        } else if (mainPriority < otherPriority) {
            return 1;
        } else {
            return compareEqualPriorities(mainTask, taskToCompare);
        }
    }
    
    private int compareEqualPriorities(Task mainTask, Task taskToCompare) {
        if (mainTask instanceof FloatTask && taskToCompare instanceof FloatTask) {
            return mainTask.getName().fullName.toLowerCase().compareTo(taskToCompare.getName().fullName.toLowerCase());
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
        
    /**
     * Maps status values to their priority for sorting
     * @param status Status enum as defined in ReadOnlyTask
     * @return
     */
    private int getPriority(Status status) {
        switch (status) {
        case DONE:
            return PRIORITY_DONE;
        case FLOAT:
            return PRIORITY_FLOAT;
        case PENDING:
            return PRIORITY_PENDING;
        case OVERDUE:
            return PRIORITY_OVERDUE;
        default:
            return PRIORITY_FLOAT;
        }
    }
}
