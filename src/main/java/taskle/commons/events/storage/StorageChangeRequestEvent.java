package taskle.commons.events.storage;

import taskle.commons.events.BaseEvent;
import taskle.model.ReadOnlyTaskManager;

//@@author A0140047U
// Indicates a request to change storage file/directory
public class StorageChangeRequestEvent extends BaseEvent {

    private String directory;
    private ReadOnlyTaskManager taskManager;
    
    private final static String EVENT_MESSAGE = "Request to Change Storage: %1$s;";
    private final static String EVENT_MESSAGE_TASK_MANAGER = " Reset Task Manager: %1$s";
    
    public StorageChangeRequestEvent(String directory, ReadOnlyTaskManager taskManager) {
        this.directory = directory;
        this.taskManager = taskManager;
    }
    
    @Override
    public String toString() {
       return String.format(EVENT_MESSAGE, directory) + String.format(EVENT_MESSAGE_TASK_MANAGER, taskManager != null); 
    }
    
    public String getDirectory() {
        return directory;
    }

    public ReadOnlyTaskManager getTaskManager() {
        return taskManager;
    }
}
