package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.RedoCommand;

//@@author A0140047U
/**
 * RedoCommandParser class to handle parsing of redo commands
 */
public class RedoCommandParser extends CommandParser {

    public RedoCommandParser() {
        
    }
    
    @Override
    public String getCommandWord() {
        return RedoCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new RedoCommand();
    }
}
