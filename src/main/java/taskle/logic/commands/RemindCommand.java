package taskle.logic.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.history.History;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

public class RemindCommand extends Command {
    
    public static final String COMMAND_WORD = "remind";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Set or remove the reminder date of the Task identified by the index number used in the last Task listing."
            + "If only the reminder date but not the time is entered, the reminder time will default to 00:00 of the specified"
            + "reminder date.\n"
            + "Format: " + COMMAND_WORD + " task_number new_date new_time(optional)\n" + "Example: " 
            + COMMAND_WORD + " 1 29 Nov 3pm ---> for setting the reminder date\tOR\t"
            + COMMAND_WORD + " 1 clear ---> for removing the reminder date";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Set Reminder Date: %1$s";

    public final int targetIndex;

    public final Date remindDate;

    public RemindCommand(int targetIndex, Date remindDate) throws IllegalValueException {
        if(remindDate != null) {
            
        }
        this.targetIndex = targetIndex; 
        this.remindDate = remindDate;
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
        String oldRemindDate = taskToEdit.getRemindDetailsString();
        
        try {
            tasksAffected = new ArrayList<Task>();
            Task originalTask = taskToEdit.copy();
            tasksAffected.add(originalTask);
            model.editTaskRemindDate(offsetIndex, remindDate);
            tasksAffected.add((Task) taskToEdit);
            History.insert(this);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }
        ReadOnlyTask newTask = lastShownList.get(offsetIndex);
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, taskToEdit.getName() + " " 
                                            + oldRemindDate + " -> " + newTask.getRemindDetailsString()));
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
