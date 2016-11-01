package taskle.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.TaskList;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
 * Wraps all data at the task-manager level
 * Duplicates are allowed.
 */
public class TaskManager implements ReadOnlyTaskManager {

    private final TaskList tasks;

    {
        tasks = new TaskList();
    }

    public TaskManager() {
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(TaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList());
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(ReadOnlyTaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList());
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(TaskList tasks) {
        resetData(tasks.getInternalList());
    }

    public static ReadOnlyTaskManager getEmptyTaskManager() {
        return new TaskManager();
    }

    //// list overwrite operations

    public ObservableList<Task> getTasks() {
        return tasks.getInternalList();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks.getInternalList().setAll(tasks);
    }

    public void resetData(Collection<? extends ReadOnlyTask> newTasks) {
        setTasks(newTasks.stream().map(t -> t.copy()).collect(Collectors.toList()));
    }

    public void resetData(ReadOnlyTaskManager newData) {
        resetData(newData.getTaskList());
    }

    //// task-level operations

    /**
     * Adds a task to the Task manager.
     * Also checks the new task's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the task to point to those in {@link #tags}.
     * 
     * @param p task to be added
     */
    public void addTask(Task p) {
        tasks.add(p);
    }

    public boolean removeTask(ReadOnlyTask key) throws TaskList.TaskNotFoundException {
        if (tasks.remove(key)) {
            return true;
        } else {
            throw new TaskList.TaskNotFoundException();
        }
    }
    //@@author A0139402M    
    public void editTask(int index, Name newName) {
        tasks.edit(index, newName);
    }

    public void editTaskDate(int index, List<Date> dates) throws TaskNotFoundException {
        tasks.editDate(index, dates);
    }
    
    public String editTaskRemindDate(int index, Date date) throws TaskNotFoundException {
        return tasks.editRemindDate(index, date);
    }
    
    public List<Task> verifyReminder(Date currentDateTime) {
        return tasks.verifyRemindDate(currentDateTime);
    }
    
    public void dismissReminder(Date currentDateTime) {
        tasks.dismissReminder(currentDateTime);
    }
    
    //@@author A0125509H
    public void doneTask(int index, boolean targetDone) {
        tasks.done(index, targetDone);
    }
    //@@author
     
    public void unDoneTask(Task task) {
        tasks.unDone(task);
    }

    //// util methods

    @Override
    public String toString() {
        return tasks.getInternalList().size() + " tasks, ";
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return Collections.unmodifiableList(tasks.getInternalList());
    }


    @Override
    public TaskList getUniqueTaskList() {
        return this.tasks;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskManager // instanceof handles nulls
                        && this.tasks.equals(((TaskManager) other).tasks));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing
        // your own
        return Objects.hash(tasks);
    }

}
