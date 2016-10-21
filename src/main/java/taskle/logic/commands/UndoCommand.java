package taskle.logic.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import taskle.logic.commands.undo.UndoAddCommand;
import taskle.logic.commands.undo.UndoClearCommand;
import taskle.logic.commands.undo.UndoDoneCommand;
import taskle.logic.commands.undo.UndoEditCommand;
import taskle.logic.commands.undo.UndoRemoveCommand;
import taskle.logic.commands.undo.UndoRescheduleCommand;
import taskle.logic.history.History;
import taskle.model.Model;

/**
 * Undo recent command entered.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undo most recent command." + "Example: "
            + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Restored previous command: [%s %s]";
    
    public UndoCommand() {
        
    }
    
    @Override
    public CommandResult execute() {
        return null;
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
