package taskle.logic.commands;

import taskle.model.Model;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * UndoDoneCommand to handle undo of done commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoDoneCommand extends UndoCommand {

    public UndoDoneCommand() {}
    
    public CommandResult undoDone(Command command, Model model) {
        assert command != null & model != null;
        
        DoneCommand doneCommand = (DoneCommand) command;
        try {
            model.doneTask(doneCommand.targetIndex, false);
        } catch (TaskNotFoundException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return DoneCommand.COMMAND_WORD;
    }
}
