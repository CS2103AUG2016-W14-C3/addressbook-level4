package taskle.logic.commands;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.exceptions.IllegalValueException;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList.TaskNotFoundException;

//@@author A0139402M
/**
 * Edits a task identified using it's last displayed index from the task
 * manager.
 * @author zhiyong 
 */
//@@author A0139402M
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "rename";

    public static final String COMMAND_WORD_SHORT = "rn";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits an existing task in Taskle.\n"
            + "\nFormat: edit task_number new_task_name\n" + "\nExample: " + COMMAND_WORD + " 6 Pay Abel for Chicken Rice";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Renamed Task: %1$s";

    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in Taskle!";

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
        
        int offsetIndex = targetIndex - 1;
        ReadOnlyTask taskToEdit = lastShownList.get(offsetIndex);
        String oldName = taskToEdit.getName().fullName; 
        
        try {
            model.storeTaskManager(COMMAND_WORD);
            model.editTask(offsetIndex, newName);
        } catch (TaskNotFoundException pnfe) {
            model.rollBackTaskManager(false);
            assert false : "The target task cannot be missing";
        }
        
        return new CommandResult(
                String.format(MESSAGE_EDIT_TASK_SUCCESS, 
                              oldName + " -> " + newName),
                true);
    }
    
    public int getIndex() {
        return targetIndex;
    }

}
