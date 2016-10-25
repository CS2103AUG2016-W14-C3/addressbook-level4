package taskle.logic.commands;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
 * Edits a task identified using it's last displayed index from the task
 * manager.
 * @author zhiyong 
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the Task identified by the index number used in the last Task listing.\n"
            + "Format: edit task_number new_task\n" + "Example: " + COMMAND_WORD + " 1 Buy dinner";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Edited Task: %1$s";

    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the Task Manager";

    public final int targetIndex;

    public final Name newName;

    public EditCommand(int targetIndex, String newName) throws IllegalValueException {
        this.targetIndex = targetIndex;
        this.newName = new Name(newName);
    }

    @Override
    public CommandResult execute() {
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX, false);
        }

        ReadOnlyTask taskToEdit = lastShownList.get(targetIndex - 1);
        String oldName = taskToEdit.getName().fullName;
        try {
            model.storeTaskManager();
            model.editTask(targetIndex, newName);
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }
        
        return new CommandResult(
                String.format(MESSAGE_EDIT_TASK_SUCCESS, 
                              oldName + " -> " + newName),
                true);
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
    
    public int getIndex() {
        return targetIndex;
    }

}
