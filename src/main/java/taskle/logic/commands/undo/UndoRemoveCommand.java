package taskle.logic.commands.undo;

import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.Model;
import taskle.model.task.Task;

/**
 * UndoRemoveCommand to handle undo of remove commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoRemoveCommand extends UndoCommand {

    public UndoRemoveCommand() {}
    
    /**
     * Undo Remove Command by adding most recent task that were previously removed
     * @param command remove command
     * @param model current model
     * @return the command feedback
     */
    public CommandResult undoRemove(Command command, Model model) {
        assert command != null && model != null;
        
        Task task = command.getTasksAffected().get(0);
        
        model.addTask(task);

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return RemoveCommand.COMMAND_WORD;
    }
}
