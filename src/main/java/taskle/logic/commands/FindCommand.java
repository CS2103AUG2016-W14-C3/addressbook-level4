package taskle.logic.commands;

import java.util.Set;

//@@author A0909865T
/**
 * Finds and lists all tasks in task manager whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {
    
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks with names consisting of "
            + "the specified keywords (case-sensitive)\nand displays them as a list with index numbers.\n"
            + "Format: " + COMMAND_WORD + " search_query\n"
            + "Example: " + COMMAND_WORD + " meeting";

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
