package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.RescheduleCommand;

//@@author A0139402M
public class RescheduleCommandParser extends CommandParser {

    public RescheduleCommandParser() {
    }

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(RescheduleCommand.COMMAND_WORD)
               || commandWord.equals(RescheduleCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareReschedule(args);
    }

    /**
     * Prepares the reschedule command while checking for any possible errors in
     * the input given by the user.
     * 
     * @param args
     * @return the prepared reschedule command
     */
    private Command prepareReschedule(String args) {
        args = args.trim();
        int endIndex = args.indexOf(" ");
        if (endIndex == -1) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE));
        }
        String indexValue = args.substring(0, endIndex);
        Optional<Integer> index = parseIndex(indexValue);
        String newDateTime = args.substring(endIndex).trim();
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE));
        }
        
        if (newDateTime.indexOf("clear") == 0) {
            return new RescheduleCommand(index.get(), null);
        }
        
        List<Date> dates = DateParser.parse(newDateTime);
        if(dates.size() == 0 || dates.size() > 2) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE));
        }

        return new RescheduleCommand(index.get(), dates);
    }

}
