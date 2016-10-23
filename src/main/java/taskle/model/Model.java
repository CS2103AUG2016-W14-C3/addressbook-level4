package taskle.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import taskle.commons.core.UnmodifiableObservableList;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskManager newData);

    /** Returns the TaskManager */
    ReadOnlyTaskManager getTaskManager();

    /** Deletes the given task. */
    void deleteTask(ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;

    /** Edits the given task. */
    void editTask(int index, Name newName) throws UniqueTaskList.TaskNotFoundException, UniqueTaskList.DuplicateTaskException;
    
    /** Edits the date / time of the task */
    void editTaskDate(int index, List<Date> dates) throws UniqueTaskList.TaskNotFoundException;
   
    /** Edits / Sets the reminder date of the task */
    void editTaskRemindDate(int index, Date date) throws UniqueTaskList.TaskNotFoundException;
    
    /** Marks the task as done*/
    void doneTask(int index, boolean targetDone) throws UniqueTaskList.TaskNotFoundException;
    
    /** Marks the task as undone */
    void unDoneTask(Task task);
    
    /** Adds the given task */
    void addTask(Task task) throws UniqueTaskList.DuplicateTaskException;

    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();

    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();
    
    /** Updates the filter of the filtered task list to show tasks that are not done*/
    void updateFilteredListToShowAllNotDone();

    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredTaskList(Set<String> keywords);

}
