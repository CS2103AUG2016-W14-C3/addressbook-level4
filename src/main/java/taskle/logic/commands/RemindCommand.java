package taskle.logic.commands;

import java.util.Date;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList.TaskNotFoundException;

//@@author A0139402M
public class RemindCommand extends Command {
    
    public static final String COMMAND_WORD = "remind";
    public static final String COMMAND_WORD_SHORT = "rmd";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Either edits or removes the reminder date and time of an existing task in Taskle."
            + "\nIf only the reminder date but not the time is entered, the reminder time will default to 00:00 of the reminder date.\n"
            + "\nFormat: " + COMMAND_WORD + " task_number [date time]\nor\n" + COMMAND_WORD + " task_number clear" 
            + "\n\nExample: " 
            + COMMAND_WORD + " 1 29 Nov 3pm (To Edit the Reminder Date and Time)\t\nor\n"
            + COMMAND_WORD + " 1 clear (To Remove the Reminder)";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Set Reminder Date: %1$s";

    public final int targetIndex;

    public final Date remindDate;

    public RemindCommand(int targetIndex, Date remindDate) throws IllegalValueException {
        this.targetIndex = targetIndex; 
        this.remindDate = remindDate;
    }

    @Override
    public CommandResult execute() {
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
        
        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX, false);
        }
        int offsetIndex = targetIndex - 1;
        ReadOnlyTask taskToEdit = lastShownList.get(offsetIndex);
        String oldRemindDate = taskToEdit.getRemindDetailsString();
        try {
            model.storeTaskManager(COMMAND_WORD);
            String result = model.editTaskRemindDate(offsetIndex, remindDate);
            if(result != null) {
                model.rollBackTaskManager();
                indicateAttemptToExecuteIncorrectCommand(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, result));
                return new CommandResult(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, result), false);
            }
        } catch (TaskNotFoundException pnfe) {
            model.rollBackTaskManager();
            assert false : "The target task cannot be missing";
        }

        ReadOnlyTask newTask = lastShownList.get(offsetIndex);
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, taskToEdit.getName() + " " 
                                            + oldRemindDate + " -> " + newTask.getRemindDetailsString()), true);
    }    
}
