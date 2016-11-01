package taskle.logic.commands;

import java.util.Date;
import java.util.List;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.commons.util.DateFormatUtil;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList.TaskNotFoundException;

//@@author A0139402M
/**
 * Reschedule command for the user to reschedule a task / event's date and/or time or even clear it.
 * Time is optional but date is mandatory.
 * @author zhiyong
 *
 */
//@@author A0139402M
public class RescheduleCommand extends Command{

    public static final String COMMAND_WORD = "reschedule";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Reschedules or removes the date of an existing task in Taskle.\n"
            + "\nFormat: " + COMMAND_WORD + " task_number to [date time] [remind date time]\n"
            + "OR " + COMMAND_WORD + " task_number clear\n"
            + "\nExample: " + COMMAND_WORD + " 2 to 7th October 7pm remind 6th October 7pm \nOR\t\n"
            + COMMAND_WORD + " 1 clear (To Remove Date)";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Rescheduled Task: %1$s";

    public final int targetIndex;

    public final List<Date> dates;

    public RescheduleCommand(int targetIndex, List<Date> dates) throws IllegalValueException {
        this.targetIndex = targetIndex; 
        this.dates = dates;
    }

    @Override
    public CommandResult execute() {
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX,
                                     false);
        }
        int offsetIndex = targetIndex - 1;
        ReadOnlyTask taskToEdit = lastShownList.get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        try {
            model.storeTaskManager();
            model.editTaskDate(offsetIndex, dates);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }
        String newDate = getDateString(dates);
        return new CommandResult(
                String.format(MESSAGE_EDIT_TASK_SUCCESS, taskToEdit.getName() + " " 
                        + oldDetails + " -> " + newDate),
                true);
    }
    
    /**
     * Returns the formatted date from the list of dates given
     * @param dates
     * @return
     */
    private String getDateString(List<Date> dates) {
        String newDate = "";
        if(dates == null) {
            newDate = DateFormatUtil.formatDate(null);
        } else if(dates.size() == 1) {
            newDate = DateFormatUtil.formatDate(dates.get(0));
        } else if(dates.size() == 2) {
            newDate = DateFormatUtil.formatEventDates(dates.get(0), dates.get(1));
        }
        return newDate;
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}
