package taskle.logic.commands;

import taskle.logic.history.History;
import taskle.model.person.ModifiableTask;
import taskle.model.person.Task;
import taskle.model.person.UniqueTaskList;
import taskle.model.person.UniqueTaskList.DuplicateTaskException;

/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {
    
    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command."
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command: [%s %s]";
    
    public UndoCommand() {}

    @Override
    public CommandResult execute() {
        if (History.isEmpty()) {
            return new CommandResult(History.MESSAGE_EMPTY_HISTORY);
        } else {
            Command command = History.remove();
            Task task;
            
            switch (command.getCommandName()) {
                case "add":
                    task = command.getTasksAffected().get(0);
                    try {
                        taskManager.removeTask(task);
                    } catch (UniqueTaskList.TaskNotFoundException e) {
                        e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                   
                case "edit":
                    Task originalTask = command.getTasksAffected().get(0);
                    task = command.getTasksAffected().get(1);
                    try {
                        taskManager.editTask((ModifiableTask)task, originalTask.getName());
                    } catch (DuplicateTaskException e) {
                        e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                    
                case "remove":
                    task = command.getTasksAffected().get(0);
                    try {
                        taskManager.addTask(task);
                    } catch (UniqueTaskList.DuplicateTaskException e) {
                        e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                    
                case "clear":
                    for (int i = 0; i < command.getTasksAffected().size(); i++) {
                        try {
                            taskManager.addTask(command.getTasksAffected().get(i));
                        } catch (UniqueTaskList.DuplicateTaskException e) {
                            e.printStackTrace();
                        }
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), ""));
                
                default:
                    return new CommandResult(History.MESSAGE_EMPTY_HISTORY);
            }
        }
    }
    
    @Override
    public String getCommandName() {
        return COMMAND_WORD;
    }
}
