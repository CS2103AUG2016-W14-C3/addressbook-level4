package taskle.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import taskle.commons.core.UnmodifiableObservableList;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.TaskList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskManager newData);

    /** Returns the TaskManager */
    ReadOnlyTaskManager getTaskManager();

    //@@author A0139402M
    /** Deletes the given task. */
    void deleteTask(ReadOnlyTask target) throws TaskList.TaskNotFoundException;

    /** Edits the given task. */
    void editTask(int index, Name newName) throws TaskList.TaskNotFoundException;
    
    /** Edits the date / time of the task */
    void editTaskDate(int index, List<Date> dates) throws TaskList.TaskNotFoundException;
   
    /** Edits / Sets the reminder date of the task */
    String editTaskRemindDate(int index, Date date) throws TaskList.TaskNotFoundException;
    
    /** Verifies the reminder date with the current date */
    List<Task> verifyRemindDate(Date currentDateTime);
    
    /** Dismiss the reminders */
    void dismissReminder(Date currentDateTime);
    
    //@@author A0125509H
    /** Marks the task as done*/
    void doneTask(int index, boolean targetDone) throws TaskList.TaskNotFoundException;
    
    /** Adds the given task */
    void addTask(Task task);

    //@@author A0140047U
    /** Stores current TaskManager state */
    void storeTaskManager();
    
    /** Restores most recently stored TaskManager state */
    boolean restoreTaskManager();
    
    /** Undo most recently restored TaskManager state */
    boolean revertTaskManager();
    
    //@@author A0141780J
    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();

    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();
    
    /** Updates the filter of the filtered task list to show tasks filtered by predicates*/
    void updateFilteredListWithStatuses();
    
    /** Updates the filter status predicates*/
    void updateFilters(boolean pending, boolean done, boolean overdue);

    /** Updates the filter keywords predicates*/
    void updateFilters(Set<String> keywords);
    
    /** Updates the filter statuses and keywords predicates*/
    void updateFilters(Set<String>keywords, boolean pending, 
                       boolean done, boolean overdue);

}
