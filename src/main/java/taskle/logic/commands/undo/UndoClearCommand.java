package taskle.logic.commands.undo;

import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.UndoCommand;
import taskle.model.Model;
import taskle.model.task.UniqueTaskList;

/**
 * UndoClearCommand to handle undo of clear Commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoClearCommand extends UndoCommand {
    
    public UndoClearCommand() {}
 
    /**
     * Undo Clear Command by adding all tasks that were previously removed
     * @param command clear command
     * @param model current model
     * @return the command feedback
     */
    public CommandResult undoClear(Command command, Model model) {
        assert command != null && model != null;
        
        for (int i = 0; i < command.getTasksAffected().size(); i++) {
            try {
                model.addTask(command.getTasksAffected().get(i));
            } catch (UniqueTaskList.DuplicateTaskException e) {
                e.printStackTrace();
            }
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, command.getCommandWord(), ""));
    }
    
    @Override
    public String getCommandWord() {
        return ClearCommand.COMMAND_WORD;
    }
}
