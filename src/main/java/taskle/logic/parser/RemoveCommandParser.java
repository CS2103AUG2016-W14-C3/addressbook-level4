package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.RemoveCommand;

/**
 * RemoveCommandParser to handle parsing of command arguments. *
 */
public class RemoveCommandParser extends CommandParser {

    public RemoveCommandParser() {
    }

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(RemoveCommand.COMMAND_WORD)
               || commandWord.equals(RemoveCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareRemove(args);
    }
    
    //@@author A0125509H
    /**
     * Parses arguments in the context of the remove task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareRemove(String args) {
        String argsTrim = args.trim();
        String []s = argsTrim.split(" ");
        for(int i = 0; i < s.length; i++) {
            Optional<Integer> index = parseIndex(s[i]);
            if (!index.isPresent()) {
                return new IncorrectCommand(MESSAGE_INVALID_COMMAND_FORMAT);
            }
        }
        
    	return new RemoveCommand(args);
    }
}