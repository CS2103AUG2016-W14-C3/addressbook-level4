package taskle.logic.commands;

import taskle.logic.history.History;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;
import taskle.model.task.UniqueTaskList.DuplicateTaskException;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command." + "Example: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command: [%s %s]";

    public UndoCommand() {
    }

    @Override
    public CommandResult execute() {
        if (History.isEmpty()) {
            return new CommandResult(History.MESSAGE_EMPTY_HISTORY);
        } else {
            Command command = History.remove();

            switch (command.getCommandName()) {
                case AddCommand.COMMAND_WORD:
                    return undoAdd(command);

                case EditCommand.COMMAND_WORD:
                    return undoEdit(command);

                case RemoveCommand.COMMAND_WORD:
                    return undoRemove(command);

                case ClearCommand.COMMAND_WORD:
                    return undoClear(command);

                default:
                    return new CommandResult(History.MESSAGE_EMPTY_HISTORY);
            }
        }
    }

    @Override
    public String getCommandName() {
        return COMMAND_WORD;
    }

    /**
     * Undo Add Command by removing tasks that were previously added
     * 
     * @param command
     *            previous Add Command
     * @return the command feedback
     */
    public CommandResult undoAdd(Command command) {
        Task task = command.getTasksAffected().get(0);
        try {
            model.deleteTask(task);
        } catch (UniqueTaskList.TaskNotFoundException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
    }

    /**
     * Undo Edit Command by editing tasks to its original content
     * 
     * @param command
     *            previous Edit Command
     * @return the command feedback
     */
    public CommandResult undoEdit(Command command) {
        Task task = command.getTasksAffected().get(0);
        EditCommand editCommand = (EditCommand) command;
        try {
            model.editTask(editCommand.getIndex(), task.getName());
        } catch (DuplicateTaskException e) {
            e.printStackTrace();
        } catch (TaskNotFoundException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
    }

    /**
     * Undo Remove Command by adding most recent task that were previously
     * removed
     * 
     * @param command
     *            previous Remove Command
     * @return the command feedback
     */
    public CommandResult undoRemove(Command command) {
        Task task = command.getTasksAffected().get(0);
        try {
            model.addTask(task);
        } catch (UniqueTaskList.DuplicateTaskException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandName(), command.getTasksAffected().get(0).toString()));
    }

    /**
     * Undo Clear Command by adding all tasks that were previously removed
     * 
     * @param command
     *            previous Clear Command
     * @return the command feedback
     */
    public CommandResult undoClear(Command command) {
        for (int i = 0; i < command.getTasksAffected().size(); i++) {
            try {
                model.addTask(command.getTasksAffected().get(i));
            } catch (UniqueTaskList.DuplicateTaskException e) {
                e.printStackTrace();
            }
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandName(), ""));
    }
}
