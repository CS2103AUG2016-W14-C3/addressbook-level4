package taskle.logic;

import java.util.Date;
import java.util.List;

import javafx.collections.ObservableList;
import taskle.logic.commands.CommandResult;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     */
    CommandResult execute(String commandText);

    /** Returns the filtered list of tasks */
    ObservableList<ReadOnlyTask> getFilteredTaskList();

    void changeDirectory(String filePath);
    
    List<Task> verifyReminder(Date currentDateTime);
    
    void dismissReminder(Date currentDateTime);
}
