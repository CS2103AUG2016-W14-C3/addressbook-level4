package taskle.logic.commands;

import java.util.Date;
import java.util.List;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Reschedule command for the user to reschedule a task / event's date and/or time or even clear it.
 * Time is optional but date is mandatory.
 * @author zhiyong
 *
 */
public class RescheduleCommand extends Command{

    public static final String COMMAND_WORD = "reschedule";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Reschedules or remove the date of the Task identified by the index number used in the last Task listing.\n"
            + "Format: " + COMMAND_WORD + " task_number to new_date new_time(optional)\n" + "Example: " 
            + COMMAND_WORD + " 1 to 11 Nov 3pm --- for rescheduling\tOR\t"
            + COMMAND_WORD + " 1 clear --- for removing the date";

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
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
        int offsetIndex = targetIndex - 1;
        ReadOnlyTask taskToEdit = lastShownList.get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        try {
            model.editTaskDate(offsetIndex, dates);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }
        ReadOnlyTask newTask = lastShownList.get(offsetIndex);
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, taskToEdit.getName() + "\t" 
                                            + oldDetails + " -> " + newTask.getDetailsString()));
    }

}
