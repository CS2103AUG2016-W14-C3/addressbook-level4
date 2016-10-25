package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.UndoCommand;

//@@author A0140047U
/**
 * UndoCommandParser class to handle parsing of undo commands
 */
public class UndoCommandParser extends CommandParser {

    public UndoCommandParser() {
        
    }
    
    @Override
    public String getCommandWord() {
        return UndoCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new UndoCommand();
    }

}
