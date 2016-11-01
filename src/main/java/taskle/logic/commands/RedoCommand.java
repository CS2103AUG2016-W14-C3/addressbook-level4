package taskle.logic.commands;

//@@author A0140047U
/**
 * Redo recent command entered.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    
    public static final String COMMAND_WORD_SHORT = "r";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Redoes a command that was previously undone." + "Example: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Redid Undone Command.";
    
    public static final String MESSAGE_NOTHING_TO_REDO = "There is Nothing to Redo.";
    
    public RedoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        if (!model.revertTaskManager()) {
            return new CommandResult(MESSAGE_NOTHING_TO_REDO, true);
        }
        return new CommandResult(MESSAGE_SUCCESS, true);
    }
    
}
