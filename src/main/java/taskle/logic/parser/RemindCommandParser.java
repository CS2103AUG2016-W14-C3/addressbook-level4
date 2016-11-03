package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.RemindCommand;

//@@author A0139402M
public class RemindCommandParser extends CommandParser{
    

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(RemindCommand.COMMAND_WORD)
               || commandWord.equals(RemindCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareRemind(args.trim());
    }

    /**
     * Prepares the remind command while checking for any possible errors in
     * the input given by the user.
     * 
     * @param args
     * @return the prepared reschedule command
     */
    private Command prepareRemind(String args) {
        int endIndex = args.indexOf(" ");
        if (endIndex == -1) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE));
        }
        String indexValue = args.substring(0, endIndex);
        Optional<Integer> index = parseIndex(indexValue);
        String newRemindDateTime = args.substring(endIndex).trim();
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE));
        }
        
        if (newRemindDateTime.indexOf("clear") == 0) {
            return clearRemind(index);
        }
        
        List<Date> dates = DateParser.parse(newRemindDateTime);
        if(dates.size() != 1) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE));
        }
        try {
            assert dates.size() == 1;
            return new RemindCommand(index.get(), dates.get(0));
        } catch (IllegalValueException e) {
            return new IncorrectCommand(e.getMessage());
        }

    }
    
    private Command clearRemind(Optional<Integer> index) {
        try {
            return new RemindCommand(index.get(), null);
        } catch (IllegalValueException e) {
            return new IncorrectCommand(e.getMessage());
        }
    }
}
