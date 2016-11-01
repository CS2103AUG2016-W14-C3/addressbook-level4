package taskle.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import taskle.model.tag.Tag;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.TaskList;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
<<<<<<< HEAD
 * Wraps all data at the task-manager level Duplicates are not allowed (by
 * .equals comparison)
=======
 * Wraps all data at the task-manager level
 * Duplicates are allowed.
>>>>>>> refs/heads/master
 */
public class TaskManager implements ReadOnlyTaskManager {

    private final TaskList tasks;
    private final UniqueTagList tags;

    {
        tasks = new TaskList();
        tags = new UniqueTagList();
    }

    public TaskManager() {
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(TaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList(), toBeCopied.getUniqueTagList());
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(ReadOnlyTaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList(), toBeCopied.getUniqueTagList());
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(TaskList tasks, UniqueTagList tags) {
        resetData(tasks.getInternalList(), tags.getInternalList());
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

    public void setTags(Collection<Tag> tags) {
        this.tags.getInternalList().setAll(tags);
    }

    public void resetData(Collection<? extends ReadOnlyTask> newTasks, Collection<Tag> newTags) {
        setTasks(newTasks.stream().map(t -> t.copy()).collect(Collectors.toList()));
        setTags(newTags);
    }

    public void resetData(ReadOnlyTaskManager newData) {
        resetData(newData.getTaskList(), newData.getTagList());
    }

    //// task-level operations

    public void addTask(Task p) {
        syncTagsWithMasterList(p);
        tasks.add(p);
    }

    /**
     * Ensures that every tag in this task: - exists in the master list
     * {@link #tags} - points to a Tag object in the master list
     */
    private void syncTagsWithMasterList(Task task) {
        final UniqueTagList taskTags = task.getTags();
        tags.mergeFrom(taskTags);

        // Create map with values = tag object references in the master list
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        for (Tag tag : tags) {
            masterTagObjects.put(tag, tag);
        }

        // Rebuild the list of task tags using references from the master list
        final Set<Tag> commonTagReferences = new HashSet<>();
        for (Tag tag : taskTags) {
            commonTagReferences.add(masterTagObjects.get(tag));
        }
        task.setTags(new UniqueTagList(commonTagReferences));
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


    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //// util methods

    @Override
    public String toString() {
        return tasks.getInternalList().size() + " tasks, " + tags.getInternalList().size() + " tags";
        // TODO: refine later
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return Collections.unmodifiableList(tasks.getInternalList());
    }

    @Override
    public List<Tag> getTagList() {
        return Collections.unmodifiableList(tags.getInternalList());
    }

    @Override
    public TaskList getUniqueTaskList() {
        return this.tasks;
    }

    @Override
    public UniqueTagList getUniqueTagList() {
        return this.tags;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskManager // instanceof handles nulls
                        && this.tasks.equals(((TaskManager) other).tasks)
                        && this.tags.equals(((TaskManager) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing
        // your own
        return Objects.hash(tasks, tags);
    }

}
