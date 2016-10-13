package taskle.logic.commands;

import java.util.ArrayList;

import taskle.logic.history.History;
import taskle.model.TaskManager;
import taskle.model.person.Task;

/**
 * Clears the task manager.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Task Manager has been cleared!";

    public ClearCommand() {}


    @Override
    public CommandResult execute() {
        assert model != null;
        
        tasksAffected = new ArrayList<Task>();
        for (Task task : taskManager.getTasks()) {
            tasksAffected.add(task);
        }
        model.resetData(TaskManager.getEmptyTaskManager());
        History.insert(this);
        return new CommandResult(MESSAGE_SUCCESS);
    }
    
    @Override
    public String getCommandName() {
        return COMMAND_WORD;
    }
}
