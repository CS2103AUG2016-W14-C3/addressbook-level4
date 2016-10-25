package taskle.commons.events.storage;

import taskle.commons.events.BaseEvent;
import taskle.storage.TaskManagerStorage;

//@@author A0140047U
/** Indicates the Storage Location of the application has changed*/
public class StorageLocationChangedEvent extends BaseEvent {

    private TaskManagerStorage data;
    
    public StorageLocationChangedEvent(TaskManagerStorage data){
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "Directory Changed: " + data.getTaskManagerFilePath();
    }
    
    public String getChangedDirectory() {
        return data.getTaskManagerFilePath();
    }
}
