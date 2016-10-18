package taskle.logic.commands;

import taskle.model.Model;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;

/**
 * UndoRemoveCommand to handle undo of remove commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoRemoveCommand extends UndoCommand {

    public UndoRemoveCommand() {}
    
    /**
     * Undo Remove Command by adding most recent task that were previously removed
     * @return the command feedback
     */
    public CommandResult undoRemove(Command command, Model model) {
        assert command != null;
        
        Task task = command.getTasksAffected().get(0);
        System.out.println(task.toString());
        try {
            model.addTask(task);
        } catch (UniqueTaskList.DuplicateTaskException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return RemoveCommand.COMMAND_WORD;
    }
}
