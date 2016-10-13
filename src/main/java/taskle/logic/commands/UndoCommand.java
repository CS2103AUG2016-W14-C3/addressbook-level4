package taskle.logic.commands;

/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {
    
    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command."
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command";
    
    public UndoCommand() {}

    @Override
    public CommandResult execute() {
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
