package taskle.logic.commands;

import taskle.model.Model;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;
import taskle.model.task.UniqueTaskList.DuplicateTaskException;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * UndoRescheduleCommand to handle undo of reschedule commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoRescheduleCommand extends UndoCommand {

    public UndoRescheduleCommand() {}

    public CommandResult undoReschedule(Command command, Model model) {
        assert command != null;
        
        Task task = command.getTasksAffected().get(0);
        RescheduleCommand rescheduleCommand = (RescheduleCommand) command;
        try {
            model.editTaskDate(rescheduleCommand.targetIndex - 1, rescheduleCommand.dates);
        } catch (TaskNotFoundException e) {
            e.printStackTrace();
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, command.getCommandWord(), command.getTasksAffected().get(0).toString()));
    }
    
    @Override
    public String getCommandWord() {
        return RescheduleCommand.COMMAND_WORD;
    }
}
