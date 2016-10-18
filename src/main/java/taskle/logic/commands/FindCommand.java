package taskle.logic.commands;

import java.util.Set;

/**
 * Finds and lists all tasks in task manager whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks with names consisting of "
            + "the specified keywords (case-sensitive) and displays them as a list with index numbers.\n"
            + "Format: " + COMMAND_WORD + " [search_query]"
            + "Example: " + COMMAND_WORD + " meeting";

    private final Set<String> keywords;

    public FindCommand(Set<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredTaskList(keywords);
        return new CommandResult(getMessageForTaskListShownSummary(model.getFilteredTaskList().size()));
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}
