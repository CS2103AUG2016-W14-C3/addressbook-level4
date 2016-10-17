package taskle.model.task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taskle.commons.core.LogsCenter;
import taskle.commons.exceptions.DuplicateDataException;
import taskle.commons.util.CollectionUtil;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;
import taskle.ui.CommandBox;

import java.util.*;
import java.util.logging.Logger;

/**
 * A list of tasks that enforces uniqueness between its elements and does not
 * allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Task#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class UniqueTaskList implements Iterable<Task> {

    private final Logger logger = LogsCenter.getLogger(CommandBox.class);

    /**
     * Signals that an operation would have violated the 'no duplicates'
     * property of the list.
     */
    public static class DuplicateTaskException extends DuplicateDataException {
        protected DuplicateTaskException() {
            super("Operation would result in duplicate tasks");
        }
    }

    /**
     * Signals that an operation targeting a specified task in the list would
     * fail because there is no such matching task in the list.
     */
    public static class TaskNotFoundException extends Exception {
    }

    private final ObservableList<Task> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty TaskList.
     */
    public UniqueTaskList() {
    }

    /**
     * Returns true if the list contains an equivalent task as the given
     * argument.
     */
    public boolean contains(ReadOnlyTask toCheck) {
        assert toCheck != null;
        return internalList.contains(toCheck);
    }

    /**
     * Adds a task to the list.
     *
     * @throws DuplicateTaskException
     *             if the task to add is a duplicate of an existing task in the
     *             list.
     */
    public void add(Task toAdd) throws DuplicateTaskException {
        assert toAdd != null;
        if (contains(toAdd)) {
            throw new DuplicateTaskException();
        }
        internalList.add(toAdd);
    }

    /**
     * Removes the equivalent task from the list.
     *
     * @throws TaskNotFoundException
     *             if no such task could be found in the list.
     */
    public boolean remove(ReadOnlyTask toRemove) throws TaskNotFoundException {
        assert toRemove != null;
        final boolean taskFoundAndDeleted = internalList.remove(toRemove);
        if (!taskFoundAndDeleted) {
            throw new TaskNotFoundException();
        }
        return taskFoundAndDeleted;
    }

    /**
     * Edits the equivalent task in the list.
     * 
     * @param toEdit
     * @return
     */
    public void edit(int index, Name newName) throws TaskNotFoundException, UniqueTaskList.DuplicateTaskException {
        Optional<Task> toEditOp = Optional.of(internalList.get(index - 1));
        if(!toEditOp.isPresent()) {
            throw new TaskNotFoundException();
        }
        Task toEdit = toEditOp.get();
        Task testTask = new FloatTask(toEdit);
        testTask.setName(newName);
        if (contains(testTask)) {
            throw new DuplicateTaskException();
        }
        toEdit.setName(newName);
        internalList.set(index - 1, toEdit);
        logger.info("Task " + index + " edited to " + newName);
    }

    /**
     * Edits the date / time of the equivalent task in the list. 
     * For 0 dates, it modifies the task into a float task without any dates.
     * For 1 date in the List, it modifies the task into a deadline task with the appropriate deadline date.
     * For 2 dates, it modifies it into an event task with the appropriate start and end dates.
     * 
     * @param index
     * @param dates
     */
    public void editDate(int index, List<Date> dates) throws TaskNotFoundException{
        Optional<Task> toEditOp = Optional.of(internalList.get(index));
        if(!toEditOp.isPresent()) {
            throw new TaskNotFoundException();
        }
        Task toEdit = toEditOp.get();
        if (dates.size() == 0) {
            if(toEdit instanceof DeadlineTask) {
                FloatTask floatTask = ((DeadlineTask) toEdit).changeToFloatTask((DeadlineTask) toEdit);
                internalList.set(index, floatTask);
            }
            if(toEdit instanceof EventTask) {
                FloatTask floatTask = ((EventTask) toEdit).changeToFloatTask((EventTask) toEdit);
                internalList.set(index, floatTask);

            }
        }
        if (dates.size() == 1) {
            Date newDate = dates.get(0);
            if (toEdit instanceof DeadlineTask) {
                ((DeadlineTask) toEdit).setDeadlineDate(newDate);
            }
            if (toEdit instanceof EventTask) {
                DeadlineTask deadlineTask = ((EventTask) toEdit).changeToDeadlineTask((EventTask) toEdit);
                deadlineTask.setDeadlineDate(newDate);
                internalList.set(index, deadlineTask);
            }
            if (toEdit instanceof FloatTask) {
                DeadlineTask deadlineTask = ((FloatTask) toEdit).changeToDeadlineTask((FloatTask) toEdit);
                deadlineTask.setDeadlineDate(newDate);
                internalList.set(index, deadlineTask);
            }
        }
        if (dates.size() == 2) {
            Date startDate = dates.get(0);
            Date endDate = dates.get(1);
            if (toEdit instanceof EventTask) {
                ((EventTask) toEdit).setStartDate(startDate);
                ((EventTask) toEdit).setEndDate(endDate);
            }
            if (toEdit instanceof DeadlineTask) {
                EventTask eventTask = ((DeadlineTask) toEdit).changeToEventTask((DeadlineTask) toEdit);
                eventTask.setStartDate(startDate);
                eventTask.setEndDate(endDate);
                internalList.set(index, eventTask);
            }
            if (toEdit instanceof FloatTask) {
                EventTask eventTask = ((FloatTask) toEdit).changeToEventTask((FloatTask) toEdit);
                eventTask.setStartDate(startDate);
                eventTask.setEndDate(endDate);
                internalList.set(index, eventTask);
            }
        } else {
            logger.severe("Number of dates exceed 2. Unable to update.");
        }

    }

    public ObservableList<Task> getInternalList() {
        return internalList;
    }

    @Override
    public Iterator<Task> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueTaskList // instanceof handles nulls
                        && this.internalList.equals(((UniqueTaskList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}