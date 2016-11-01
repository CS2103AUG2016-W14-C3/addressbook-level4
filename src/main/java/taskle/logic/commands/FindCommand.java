package taskle.logic.commands;

import java.util.Set;

//@@author A0141780J
/**
 * Finds and lists all tasks in task manager whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {
    
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks in Taskle that both contain the keywords and fulfill the specific status."
            + "\n\nFormat: " + COMMAND_WORD + " keywords [-status]\n"
            + "\nExample: " + COMMAND_WORD + " books";

    private final Set<String> keywords;
    
    // Fields for whether to show the corresponding statuses
    private boolean showPending = true;
    private boolean showDone = false;
    private boolean showOverdue = true;

    public FindCommand(Set<String> keywords) {
        this.keywords = keywords;
    }
    
    public FindCommand(Set<String> keywords, boolean showPending, 
                       boolean showDone, boolean showOverdue) {
        this.keywords = keywords;
        this.showPending = showPending;
        this.showDone = showDone;
        this.showOverdue = showOverdue;
    }

    @Override
    public CommandResult execute() {
        model.updateFilters(keywords, showPending, showDone, showOverdue);
        return new CommandResult(
                getMessageForTaskListShownSummary(
                        model.getFilteredTaskList().size()),
                true);
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}
