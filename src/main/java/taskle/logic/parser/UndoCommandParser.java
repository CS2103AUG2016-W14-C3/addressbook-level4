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
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(UndoCommand.COMMAND_WORD)
               || commandWord.equals(UndoCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return new UndoCommand();
    }

}
