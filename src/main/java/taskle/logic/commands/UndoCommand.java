package taskle.logic.commands;

//@@author A0140047U
/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undoes the most recent command." + "\n\nExample: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored Previous Command!";
    
    public static final String MESSAGE_EMPTY_HISTORY = "There is Nothing to Undo!";
    
    public UndoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        if (!model.restoreTaskManager()) {
            return new CommandResult(MESSAGE_EMPTY_HISTORY, true);
        }
        return new CommandResult(MESSAGE_SUCCESS, true);
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
