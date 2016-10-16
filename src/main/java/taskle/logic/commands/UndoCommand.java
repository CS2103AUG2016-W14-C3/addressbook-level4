package taskle.logic.commands;

import taskle.logic.history.History;
import taskle.model.person.Task;
import taskle.model.person.UniqueTaskList;
import taskle.model.person.UniqueTaskList.DuplicateTaskException;
import taskle.model.person.UniqueTaskList.TaskNotFoundException;

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
                        model.deleteTask(task);
                    } catch (UniqueTaskList.TaskNotFoundException e) {
                        e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                   
                case "edit":
                    Task originalTask = command.getTasksAffected().get(0);
                    task = command.getTasksAffected().get(1);
                    EditCommand editCommand = (EditCommand) command;
                    try {
                        model.editTask(editCommand.getIndex(), originalTask.getName());
                    } catch (DuplicateTaskException e) {
                        e.printStackTrace();
                    } catch (TaskNotFoundException e) {
                       e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                    
                case "remove":
                    task = command.getTasksAffected().get(0);
                    try {
                        model.addTask(task);
                    } catch (UniqueTaskList.DuplicateTaskException e) {
                        e.printStackTrace();
                    }
                    return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
                    
                case "clear":
                    for (int i = 0; i < command.getTasksAffected().size(); i++) {
                        try {
                            model.addTask(command.getTasksAffected().get(i));
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
