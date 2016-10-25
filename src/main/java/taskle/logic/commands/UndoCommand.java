package taskle.logic.commands;

/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command." + "Example: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command.";
    
    public static final String MESSAGE_EMPTY_HISTORY = "Empty History. Nothing to Undo.";
    
    public UndoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        if (!model.restoreTaskManager()) {
            return new CommandResult(MESSAGE_EMPTY_HISTORY);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
