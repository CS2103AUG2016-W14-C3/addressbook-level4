package taskle.logic.commands;

import taskle.logic.commands.undo.UndoAddCommand;
import taskle.logic.commands.undo.UndoClearCommand;
import taskle.logic.commands.undo.UndoDoneCommand;
import taskle.logic.commands.undo.UndoEditCommand;
import taskle.logic.commands.undo.UndoRemoveCommand;
import taskle.logic.commands.undo.UndoRescheduleCommand;
import taskle.logic.history.History;

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
        if (History.isEmpty()) {
            return new CommandResult(History.MESSAGE_EMPTY_HISTORY, false);
        } else {
            Command command = History.remove();
            
            switch (command.getCommandWord()) {
            
                case AddCommand.COMMAND_WORD:
                    return new UndoAddCommand().undoAdd(command, model);
            
                case EditCommand.COMMAND_WORD:
                    return new UndoEditCommand().undoEdit(command, model);
            
                case RemoveCommand.COMMAND_WORD:
                    return new UndoRemoveCommand().undoRemove(command, model);
            
                case ClearCommand.COMMAND_WORD:
                    return new UndoClearCommand().undoClear(command, model);
                
                case RescheduleCommand.COMMAND_WORD:
                    return new UndoRescheduleCommand().undoReschedule(command, model);
                
                case DoneCommand.COMMAND_WORD:
                    return new UndoDoneCommand().undoDone(command, model);
                    
                default:
                    return new CommandResult(History.MESSAGE_EMPTY_HISTORY, false);
            }
        }
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
