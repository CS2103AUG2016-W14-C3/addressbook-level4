package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.Command;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.RescheduleCommand;

public class RescheduleCommandParser extends CommandParser {
    
    private static final Pattern RESC_ARGS_FORMAT = Pattern.compile("(?<taskNumber>.+\\s(?=to)|.+$)" 
                                                                + "(?<dateTime>(?=to).*)*");
    
    public RescheduleCommandParser() {
    }
    
    @Override
    public String getCommandWord() {
        return RescheduleCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return prepareReschedule(args);
    }
    
    private Command prepareReschedule(String args) {
        args = args.trim();
        int endIndex = args.indexOf(" ");
        if (endIndex == -1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  EditCommand.MESSAGE_USAGE));
        }
        String indexValue = args.substring(0, endIndex);
        Optional<Integer> index = parseIndex(indexValue);
        String newDateTime = args.substring(endIndex).trim();
        if(newDateTime.indexOf("to") != 0 || !index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    RescheduleCommand.MESSAGE_USAGE));
        }
        else {
            List<Date> dateTimeDates = DateParser.parse(newDateTime);
            try {
                return new RescheduleCommand(index.get(), dateTimeDates);
            } catch (IllegalValueException e) {
                return new IncorrectCommand(e.getMessage()); 
            }
        }
    }

}
