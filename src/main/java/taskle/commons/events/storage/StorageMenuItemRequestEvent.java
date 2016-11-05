package taskle.commons.events.storage;

import taskle.commons.events.BaseEvent;

public class StorageMenuItemRequestEvent extends BaseEvent {

    private String command;
    
    public StorageMenuItemRequestEvent(String command) {
        this.command = command;
    }
    
    @Override
    public String toString() {
        return "Storage Change requested from Menu Interface.";
    }
    
    public String getCommand() {
        return command;
    }

}
