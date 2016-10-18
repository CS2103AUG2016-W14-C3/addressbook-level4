package taskle.logic.commands;

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
     * @return the command feedback
     */
    public CommandResult undoClear(Command command, Model moddel) {
        assert command != null;
        
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
