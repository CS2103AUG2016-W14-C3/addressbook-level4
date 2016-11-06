package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.RedoCommand;

//@@author A0140047U
// RedoCommandParser class to handle parsing of redo commands
public class RedoCommandParser extends CommandParser {

    public RedoCommandParser() {
        
    }
    
    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(RedoCommand.COMMAND_WORD)
                || commandWord.equals(RedoCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return new RedoCommand();
    }
}
