package taskle.logic.commands;

/**
 * Lists all tasks in the Task Manager to the user.
 */
//@@author A0141780J
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_LIST_SUCCESS = 
            "Listed %1$s tasks";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Lists all tasks with the specified statuses\n"
            + "Format: " + COMMAND_WORD + " [-status]\n"
            + "Note: You can have more than 1 statuses or none at all\n"
            + "If not status is specified, pending and overdue items will be listed"
            + "Example: " + COMMAND_WORD + " -done -pending";
    
    // Fields for whether to show the corresponding statuses
    private final boolean showPending;
    private final boolean showDone;
    private final boolean showOverdue;

    public ListCommand(boolean pending, boolean done, boolean overdue) {
        this.showPending = pending;
        this.showDone = done;
        this.showOverdue = overdue;
    }

    @Override
    public CommandResult execute() {
        model.updateFilters(showPending, showDone, showOverdue);
        
        String[] messageArray = new String[] {
                "Not Pending", "Not Done", "Not Overdue"
        };
        
        if (showPending) {
            messageArray[0] = "Pending";
        }
        
        if (showDone) {
            messageArray[1] = "Done";
        }
        
        if (showOverdue) {
            messageArray[2] = "Overdue";
        }
        
        String message = String.join(", ", messageArray);
        
        return new CommandResult(String.format(MESSAGE_LIST_SUCCESS, message), 
                                 true);
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
