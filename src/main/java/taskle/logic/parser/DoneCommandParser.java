package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import taskle.logic.commands.Command;
import taskle.logic.commands.DoneCommand;
import taskle.logic.commands.IncorrectCommand;

/**
 * Command Parser for done commands.
 * @author Abel
 *
 */
//@@author A0125509H
public class DoneCommandParser extends CommandParser {

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(DoneCommand.COMMAND_WORD)
               || commandWord.equals(DoneCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareDone(args);
    }
    
    /**
     * Prepares done command using arguments
     *
     * @param args full command args string
     * @return the prepared done command
     */
    private Command prepareDone(String arguments) {

        Optional<Integer> index = parseIndex(arguments);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE));
        }

        return new DoneCommand(index.get(), true);
    }

}
