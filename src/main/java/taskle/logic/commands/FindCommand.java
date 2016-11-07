package taskle.logic.commands;

import java.util.Set;

//@@author A0141780J
/**
 * Finds and lists all tasks in task manager whose name contains any 
 * of the argument keywords. Keyword matching is case sensitive.
 */
public class FindCommand extends Command {
    
    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_WORD_SHORT ="f";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks in Taskle that both contain the keywords and fulfill the specific status."
            + "\n\nFormat: " + COMMAND_WORD + " keywords [-status]\n"
            + "\nExample: " + COMMAND_WORD + " books";

    private final Set<String> keywords;
    
    // Fields for whether to show the corresponding statuses
    private boolean isPendingShown = true;
    private boolean isDoneShown = false;
    private boolean isOverdueShown = true;

    public FindCommand(Set<String> keywords) {
        this.keywords = keywords;
    }
    
    public FindCommand(Set<String> keywords, boolean isPendingShown, 
                       boolean isDoneShown, boolean isOverdueShown) {
        this.keywords = keywords;
        this.isPendingShown = isPendingShown;
        this.isDoneShown = isDoneShown;
        this.isOverdueShown = isOverdueShown;
    }

    @Override
    public CommandResult execute() {
        model.updateFilters(keywords, isPendingShown, isDoneShown, isOverdueShown);
        return new CommandResult(
                getMessageForTaskListShownSummary(
                        model.getFilteredTaskList().size()),
                true);
    }
    
}
