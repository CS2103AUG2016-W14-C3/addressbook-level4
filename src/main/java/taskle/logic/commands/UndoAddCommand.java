package taskle.logic.commands;

import taskle.model.Model;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;

/**
 * UndoAddCommand to handle undo of Add commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoAddCommand extends UndoCommand {

    public UndoAddCommand() {}
    
    /**
     * Undo Add Command by removing tasks that were previously added
     * @return feedback of undo operation
     */
    public CommandResult undoAdd(Command command, Model model) {
        assert command != null;
        
        Task task = command.getTasksAffected().get(0);
        try {
            model.deleteTask(task);
        } catch (UniqueTaskList.TaskNotFoundException e) {
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
