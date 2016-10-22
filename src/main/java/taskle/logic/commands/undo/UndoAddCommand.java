package taskle.logic.commands.undo;

import taskle.logic.commands.AddCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.UndoCommand;
import taskle.model.Model;
import taskle.model.task.Task;
import taskle.model.task.TaskList;

/**
 * UndoAddCommand to handle undo of Add commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoAddCommand extends UndoCommand {

    public UndoAddCommand() {}
    
    /**
     * Undo Add Command by removing tasks that were previously added
     * @param command add command
     * @param model current model
     * @return feedback of undo operation
     */
    public CommandResult undoAdd(Command command, Model model) {
        assert command != null && model != null;
        
        Task task = command.getTasksAffected().get(0);
        try {
            model.deleteTask(task);
        } catch (TaskList.TaskNotFoundException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return AddCommand.COMMAND_WORD;   
    }

}
