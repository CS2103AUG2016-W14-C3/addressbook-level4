package taskle.logic.commands;


/**
 * Lists all tasks in the Task Manager to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all tasks";

    public ListCommand() {}

    @Override
    public CommandResult execute() {
        model.updateFilteredListToShowAllNotDone();
        return new CommandResult(MESSAGE_SUCCESS, true);
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
