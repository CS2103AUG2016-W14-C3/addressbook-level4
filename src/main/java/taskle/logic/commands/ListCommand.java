package taskle.logic.commands;

import taskle.commons.util.StatusFormatUtil;

/**
 * Lists all tasks in the Task Manager to the user.
 */
//@@author A0141780J
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_WORD_SHORT = "l";

    public static final String MESSAGE_LIST_SUCCESS = 
            "Listed %1$s tasks";
    
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Lists all tasks with the specified statuses\n"
            + "\nFormat: " + COMMAND_WORD + " [-status]\n"
            + "Note: You can have more than 1 statuses or none at all\n"
            + "If no status is specified, pending and overdue items will be listed\n"
            + "\nExample: " + COMMAND_WORD + " -done -pending";
    
    // Fields for whether to show the corresponding statuses
    private final boolean isPendingShown;
    private final boolean isDoneShown;
    private final boolean isOverdueShown;

    public ListCommand(boolean isPendingShown, boolean isDoneShown, 
                       boolean isOverdueShown) {
        this.isPendingShown = isPendingShown;
        this.isDoneShown = isDoneShown;
        this.isOverdueShown = isOverdueShown;
    }

    @Override
    public CommandResult execute() {
        model.updateFilters(isPendingShown, isDoneShown, isOverdueShown);
        String message = StatusFormatUtil.getFormattedFilters(
                isPendingShown, isDoneShown, isOverdueShown);
        return new CommandResult(String.format(MESSAGE_LIST_SUCCESS, message), 
                                 true);
    }

}
