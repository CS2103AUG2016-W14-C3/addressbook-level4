package taskle.logic.commands.undo;

import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.DoneCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.Model;
import taskle.model.task.Task;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
 * UndoDoneCommand to handle undo of done commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoDoneCommand extends UndoCommand {

    public UndoDoneCommand() {}
    
    public CommandResult undoDone(Command command, Model model) {
        assert command != null & model != null;
        
        Task task = command.getTasksAffected().get(0);
        model.unDoneTask(task);
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return DoneCommand.COMMAND_WORD;
    }
}
