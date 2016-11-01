package taskle.model.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taskle.commons.core.LogsCenter;
import taskle.commons.core.Messages;
import taskle.commons.util.CollectionUtil;
import taskle.commons.util.TaskUtil;
import taskle.ui.CommandBox;
import taskle.ui.SystemTray;

/**
 * A list of tasks that does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Task#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class TaskList implements Iterable<Task> {

    private final Logger logger = LogsCenter.getLogger(CommandBox.class);

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
    public TaskList() {
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
     */
    public void add(Task toAdd) {
        assert toAdd != null;
        internalList.add(toAdd);
        refreshInternalList();
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

        refreshInternalList();
        return taskFoundAndDeleted;
    }

    // @@author A0125509H
    public void done(int index, boolean taskDone) {
        Task toEdit = internalList.get(index);
        toEdit.setTaskDone(taskDone);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " Done! ");
        refreshInternalList();
    }
    // @@author

    public void unDone(Task taskToUndo) {
        int targetIndex = internalList.indexOf(taskToUndo);
        taskToUndo.setTaskDone(false);
        internalList.set(targetIndex, taskToUndo);
        refreshInternalList();
    }

    // @@author A0139402M
    /**
     * Edits the equivalent task in the list.
     * 
     * @param toEdit
     * @return
     */
    public void edit(int index, Name newName) {
        Task toEdit = internalList.get(index);
        FloatTask testTask = new FloatTask(toEdit);
        testTask.setName(newName);

        toEdit.setName(newName);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " edited to " + newName);
        refreshInternalList();
    }

    /**
     * Edits the date / time of the equivalent task in the list. For null, it
     * modifies the task into a float task without any dates. For 1 date in the
     * List, it modifies the task into a deadline task with the appropriate
     * deadline date. For 2 dates, it modifies it into an event task with the
     * appropriate start and end dates.
     * 
     * @param index
     * @param dates
     */
    public void editDate(int index, List<Date> dates) throws TaskNotFoundException {
        Optional<Task> toEditOp = Optional.of(internalList.get(index));
        if (!toEditOp.isPresent()) {
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
        refreshInternalList();
    }

    /**
     * Edits the reminder date for the task in the list
     * 
     * @param index
     * @param date
     * @throws TaskNotFoundException
     */
    public String editRemindDate(int index, Date date) throws TaskNotFoundException {
        Optional<Task> toEditOp = Optional.of(internalList.get(index));

        if (!toEditOp.isPresent()) {
            throw new TaskNotFoundException();
        }
        Task toEdit = toEditOp.get();
        
        if(checkInvalidRemindDate(toEdit, date)) {
            return Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE;
        }
        toEdit.setRemindDate(date);
        internalList.set(index, toEdit);
        logger.info("Task " + index + " edited reminder date to " + toEdit.getRemindDetailsString());
        return null;
    }
    
    /**
     * Method to check if the reminder date is after the end date of the task 
     * @param task
     * @param remindDate
     * @return
     */
    private boolean checkInvalidRemindDate(Task task, Date remindDate) {
        if(task instanceof DeadlineTask) {
            if(remindDate.after(((DeadlineTask) task).getDeadlineDate())) {
                return true;
            }
            return false;
        }
        if(task instanceof EventTask) {
            if(remindDate.after(((EventTask) task).getEndDate())) {
                return true;
            }
            return false;
        }
        return false;
    }
    /**
     * Method to check through the current list of reminders for each task
     * and compare with the current system date time.
     * @param currentDateTime
     * @return list of reminders that are before the current system date time
     */
    public List<Task> verifyRemindDate(Date currentDateTime) {
        List<Task> remindTaskList = new ArrayList<>();
        for (int i = 0; i < internalList.size(); i++) {
            Task currentTask = internalList.get(i);
            Date remindDate = currentTask.getRemindDate();
            if (remindDate != null) {
                if (currentDateTime.after(remindDate)) {
                    remindTaskList.add(currentTask);
                }
            }
        }
        logger.info("Return List of Tasks to be Reminded. Size: " + remindTaskList.size());
        return remindTaskList;
    }

    /**
     * Sets the visibility of the list of reminders given.
     * @param tasks
     * @param isVisible
     */
    public void dismissReminder(Date currentDateTime) {
        assert currentDateTime != null;
        for (int i = 0; i < internalList.size(); i++) {
            Task currentTask = internalList.get(i);
            Date remindDate = currentTask.getRemindDate();
            if (remindDate != null) {
                if (currentDateTime.after(remindDate)) {
                    currentTask.setRemindDate(null);
                    internalList.set(i, currentTask);
                }
            }
        }
        logger.info("Tasks with reminders past have reminders removed.");
    }
    
    // @@author A0140047U
    public void refreshInternalList() {
        internalList.sort(new TaskComparator());
    }

    // @@author
    /**
     * Method to update the internal list with a float task
     * 
     * @param toEdit
     * @param index
     */
    private void updateListFloat(Task toEdit, int index) {
        if (toEdit instanceof DeadlineTask) {
            FloatTask floatTask = TaskUtil.deadlineChangeToFloat((DeadlineTask) toEdit);
            internalList.set(index, floatTask);
        }
        if (toEdit instanceof EventTask) {
            FloatTask floatTask = TaskUtil.eventChangeToFloat((EventTask) toEdit);
            internalList.set(index, floatTask);
        }
        logger.info("Updated Task to FloatTask with no date");
    }

    /**
     * Method to update the internal list with a deadline task
     * 
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
     * 
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
                || (other instanceof TaskList // instanceof handles nulls
                        && this.internalList.equals(((TaskList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
