package taskle.logic.commands;

import java.util.ArrayList;
import java.util.Collections;

import taskle.commons.core.Messages;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
 * Deletes a task identified using it's last displayed index from the task manager.
 */

public class RemoveCommand extends Command {

    public static final String COMMAND_WORD = "remove";
    
    public static final String COMMAND_WORD_SHORT = "rm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes an existing task in Taskle.\n"
            + "\nFormat: remove task_number\n"
            + "\nExample: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Removed Task(s): %1$s";

    //@@author A0125509H
    public final String targetIndexes;
    private int arraySize;
    private String[] s;
    private ArrayList<Integer> sInt = new ArrayList<Integer>();
    
    public RemoveCommand(String targetIndexes) {
        this.targetIndexes = targetIndexes;
        
        String argsTrim = targetIndexes.trim();
        s = argsTrim.split(" ");
        for(int i=0; i<s.length; i++) {   
            sInt.add(Integer.parseInt(s[i]));
        }
        
        Collections.sort(sInt);
        Collections.reverse(sInt);
        
        arraySize = s.length;
    }


    @Override
    public CommandResult execute() {
        model.storeTaskManager(COMMAND_WORD);
        
        for(int i=0; i<arraySize; i++) {
            UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();
    
            if (lastShownList.size() < sInt.get(i)) {
                indicateAttemptToExecuteIncorrectCommand(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
                return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX, false);
            }
    
            ReadOnlyTask taskToDelete = lastShownList.get(sInt.get(i) - 1);
    
            try {
                 model.deleteTask(taskToDelete);
            } catch (TaskNotFoundException pnfe) {
                model.rollBackTaskManager(false);
                assert false : "The target task cannot be missing";
            }
        }

        String message = String.join(", ", s);
        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS, message), true);
    }
    
}
