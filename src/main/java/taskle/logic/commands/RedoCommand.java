package taskle.logic.commands;

import taskle.commons.core.Messages;

//@@author A0140047U
/**
 * Redo recent command entered.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String COMMAND_WORD_SHORT = "r";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Redo a command that was previously undone." + "Example: "
            + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Redo previous command.";
    public static final String MESSAGE_FAILURE = "An error occured when running redo command.";
    public static final String MESSAGE_NOTHING_TO_REDO = "There is nothing to redo.";
    
    private static final int STATUS_EMPTY_HISTORY = 0;
    private static final int STATUS_ERROR_HISTORY = -1;
    
    public RedoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        switch (model.revertTaskManager()) {
        case STATUS_ERROR_HISTORY:
            indicateAttemptToExecuteIncorrectCommand(Messages.MESSAGE_CONFIG_ERROR);
            return new CommandResult(MESSAGE_FAILURE, false);
        case STATUS_EMPTY_HISTORY:
            indicateAttemptToExecuteIncorrectCommand(MESSAGE_NOTHING_TO_REDO);
            return new CommandResult(MESSAGE_FAILURE, false);
        default:
            return new CommandResult(MESSAGE_SUCCESS, true);
    }
    }
    
}
