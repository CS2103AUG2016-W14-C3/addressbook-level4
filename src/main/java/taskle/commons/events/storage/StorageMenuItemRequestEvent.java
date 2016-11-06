package taskle.commons.events.storage;

import taskle.commons.events.BaseEvent;

//@@author A0140047U
/**
 * Indicates a request to change storage file/directory from clicking menu items
 */
public class StorageMenuItemRequestEvent extends BaseEvent {

    private String command;
    private boolean isValidStatus;
    
    public StorageMenuItemRequestEvent(String command, boolean isValidStatus) {
        this.command = command;
        this.isValidStatus = isValidStatus;
    }
    
    @Override
    public String toString() {
        return "Storage Change requested from Menu Interface.";
    }
    
    public String getCommand() {
        return command;
    }
    
    public boolean isValid() {
        return isValidStatus;
    }

}
