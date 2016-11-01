package taskle.logic.commands;

//@@author A0140047U
/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    
    public static final String COMMAND_WORD_SHORT = "u";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command." + "Example: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command.";
    
    public static final String MESSAGE_EMPTY_HISTORY = "Empty History. Nothing to Undo.";
    
    public UndoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        if (!model.restoreTaskManager()) {
            return new CommandResult(MESSAGE_EMPTY_HISTORY, true);
        }
        return new CommandResult(MESSAGE_SUCCESS, true);
    }
    
}
