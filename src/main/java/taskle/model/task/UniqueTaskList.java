package taskle.model.task;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taskle.commons.core.LogsCenter;
import taskle.commons.exceptions.DuplicateDataException;
import taskle.commons.util.CollectionUtil;
import taskle.commons.util.TaskUtil;
import taskle.ui.CommandBox;

/**
 * A list of tasks that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Task#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class UniqueTaskList implements Iterable<Task> {
    
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    
    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateTaskException extends DuplicateDataException {
        protected DuplicateTaskException() {
            super("Operation would result in duplicate tasks");
        }
    }

    /**
     * Signals that an operation targeting a specified task in the list would fail because
     * there is no such matching task in the list.
     */
    public static class TaskNotFoundException extends Exception {}

    private final ObservableList<Task> internalList = FXCollections.observableArrayList();
    
    /**
     * Constructs empty TaskList.
     */
    public UniqueTaskList() {}

    /**
     * Returns true if the list contains an equivalent task as the given argument.
     */
    public boolean contains(ReadOnlyTask toCheck) {
        assert toCheck != null;
        return internalList.contains(toCheck); 
    }

    /**
     * Adds a task to the list.
     *
     * @throws DuplicateTaskException if the task to add is a duplicate of an existing task in the list.
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
     * @throws TaskNotFoundException if no such task could be found in the list.
     */
    public boolean remove(ReadOnlyTask toRemove) throws TaskNotFoundException {
        assert toRemove != null;
        final boolean taskFoundAndDeleted = internalList.remove(toRemove);
        if (!taskFoundAndDeleted) {
            throw new TaskNotFoundException();
        }
        return taskFoundAndDeleted;
    }
    
    public void done(int index, boolean taskDone) {
        Task toEdit = internalList.get(index);
        toEdit.setTaskDone(taskDone);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " Done! ");
    }
    
    public void unDone(Task taskToUndo) {
        int targetIndex = internalList.indexOf(taskToUndo);
        taskToUndo.setTaskDone(false);
        internalList.set(targetIndex, taskToUndo);
    }
    
    /**
     * Edits the equivalent task in the list.
     * @param toEdit
     * @return
     */
    public void edit(int index, Name newName) throws UniqueTaskList.DuplicateTaskException {
        Task toEdit = internalList.get(index);
        FloatTask testTask = new FloatTask(toEdit);
        testTask.setName(newName);
        if(contains(testTask)) {
            throw new DuplicateTaskException();
        }
        toEdit.setName(newName);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " edited to " + newName);
    }
    
    /**
     * Edits the reminder date for the task in the list
     * @param index
     * @param date
     * @throws TaskNotFoundException
     */
    public void editRemindDate(int index, Date date) throws TaskNotFoundException {
        Optional<Task> toEditOp = Optional.of(internalList.get(index));
        
        if(!toEditOp.isPresent()) {
            throw new TaskNotFoundException();
        }
        Task toEdit = toEditOp.get();
        toEdit.setRemindDate(date);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " edited reminder date to " + toEdit.getRemindDetailsString());
    }
    
    /**
     * Edits the date / time of the equivalent task in the list. 
     * For null, it modifies the task into a float task without any dates.
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
        if (dates == null) {
            updateListFloat(toEdit, index);
        } else if (dates.size() == 1) {
            updateListDeadline(toEdit, index, dates);
        } else if (dates.size() == 2) {
            updateListEvent(toEdit, index, dates);
        } else {
            logger.severe("Number of dates is either 0 or exceed 2. Unable to update.");
        }

    }

    /**
     * Method to update the internal list with a float task
     * @param toEdit
     * @param index
     */
    private void updateListFloat(Task toEdit, int index) {
        if(toEdit instanceof DeadlineTask) {
            FloatTask floatTask = TaskUtil.deadlineChangeToFloat((DeadlineTask) toEdit);
            internalList.set(index, floatTask);
        }
        if(toEdit instanceof EventTask) {
            FloatTask floatTask = TaskUtil.eventChangeToFloat((EventTask) toEdit);
            internalList.set(index, floatTask);
        }
        logger.info("Updated Task to FloatTask with no date");
    }
    
    /**
     * Method to update the internal list with a deadline task
     * @param toEdit
     * @param index
     * @param dates
     */
    private void updateListDeadline(Task toEdit, int index, List<Date> dates) {
        Date newDate = dates.get(0);
        if (toEdit instanceof DeadlineTask) {
            ((DeadlineTask) toEdit).setDeadlineDate(newDate);
            internalList.set(index, toEdit);
        }
        
        if (toEdit instanceof EventTask) {
            DeadlineTask deadlineTask = TaskUtil.eventChangeToDeadline((EventTask) toEdit);
            deadlineTask.setDeadlineDate(newDate);
            internalList.set(index, deadlineTask);
        }
        
        if (toEdit instanceof FloatTask) {
            DeadlineTask deadlineTask = TaskUtil.floatChangeToDeadline((FloatTask) toEdit);
            deadlineTask.setDeadlineDate(newDate);
            internalList.set(index, deadlineTask);
        }
        
        logger.info("Updated Task to DeadlineTask with 1 date");
    }
    
    /**
     * Method to update the internal list with an event task
     * @param toEdit
     * @param index
     * @param dates
     */
    private void updateListEvent(Task toEdit, int index, List<Date> dates) {
        Date startDate = dates.get(0);
        Date endDate = dates.get(1);
        if (toEdit instanceof EventTask) {
            ((EventTask) toEdit).setStartDate(startDate);
            ((EventTask) toEdit).setEndDate(endDate);
            internalList.set(index, toEdit);
        }
        
        if (toEdit instanceof DeadlineTask) {
            EventTask eventTask = TaskUtil.deadlineChangeToEvent((DeadlineTask) toEdit);
            eventTask.setStartDate(startDate);
            eventTask.setEndDate(endDate);
            internalList.set(index, eventTask);
        }
        
        if (toEdit instanceof FloatTask) {
            EventTask eventTask = TaskUtil.floatChangeToEvent((FloatTask) toEdit);
            eventTask.setStartDate(startDate);
            eventTask.setEndDate(endDate);
            internalList.set(index, eventTask);
        }
        
        logger.info("Updated Task to EventTask with 2 dates");
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
                && this.internalList.equals(
                ((UniqueTaskList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}