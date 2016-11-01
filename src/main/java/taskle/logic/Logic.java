package taskle.logic;

import javafx.collections.ObservableList;
import taskle.commons.events.storage.DataSavingExceptionEvent;
import taskle.commons.events.storage.StorageChangeRequestEvent;
import taskle.logic.commands.CommandResult;
import taskle.model.ReadOnlyTaskManager;
import taskle.model.task.ReadOnlyTask;

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

    //@@author A0140047U
    /** Updates directory to given filePath */
    void changeDirectory(String filePath);
    
    /** Resets Model based on given data */
    void resetModel(ReadOnlyTaskManager taskManager);
    
    //Changes directory and reset model, if necessary
    void handleStorageChangeRequestEvent(StorageChangeRequestEvent srce);
}
