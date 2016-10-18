package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.UndoCommand;

/**
 * UndoCommandParser class to handle parsing of undo commands
 * @author Muhammad Hamsyari
 *
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
