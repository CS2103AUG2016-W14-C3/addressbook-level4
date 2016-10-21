package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.RedoCommand;

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
