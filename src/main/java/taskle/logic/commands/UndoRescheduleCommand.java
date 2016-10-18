package taskle.logic.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import taskle.model.Model;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * UndoRescheduleCommand to handle undo of reschedule commands
 * @author Muhammad Hamsyari
 *
 */
public class UndoRescheduleCommand extends UndoCommand {

    public UndoRescheduleCommand() {}

    /**
     * Undo Reschedule Command restores original dates into recently rescheduled tasks
     * @param command reschedule command
     * @param model current model
     * @return
     */
    public CommandResult undoReschedule(Command command, Model model) {
        assert command != null && model != null;
        
        Task task = command.getTasksAffected().get(0);
        RescheduleCommand rescheduleCommand = (RescheduleCommand) command;
        
        try {
            List<Date> originalDates = new ArrayList<>();
            
            if (task instanceof FloatTask) {
                model.editTaskDate(rescheduleCommand.targetIndex, null);
            } else if (task instanceof DeadlineTask) {
                originalDates.add(((DeadlineTask) task).getDeadlineDate());
                model.editTaskDate(rescheduleCommand.targetIndex, originalDates);
            } else {
                originalDates.add(((EventTask) task).getStartDate());
                originalDates.add(((EventTask) task).getEndDate());
                model.editTaskDate(rescheduleCommand.targetIndex, originalDates);
            }
        
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
