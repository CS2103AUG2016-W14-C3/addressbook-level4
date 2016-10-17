package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;

/**
 * AddCommandParser class to handle parsing of add commands.
 * @author Abel
 *
 */
public class AddCommandParser extends CommandParser {
    
    private static final String ADD_ARGS_NAME_GROUP = "name";
    private static final String ADD_ARGS_DATE_DEADLINE_GROUP = "dateDeadline";
    private static final String ADD_ARGS_DATE_EVENT_GROUP = "dateEvent";
    
    // Groups into name (all words until last by|from|on), 
    // dateFrom (all words after from containing from at the start)
    // or dateBy (all words after by containing by at the start).
    private static final Pattern ADD_ARGS_FORMAT = 
            Pattern.compile("(?<name>.+\\s(?=by|from|on)|.+$)"
                    + "(?<dateEvent>(?=from|on).*)*"
                    + "(?<dateDeadline>(?=by).*)*");

    public AddCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return AddCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return prepareAdd(args);
    }
    
    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        final Matcher matcher = ADD_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  AddCommand.MESSAGE_USAGE));
        }
        
        // Get respective name and date Strings
        String nameString = matcher.group(ADD_ARGS_NAME_GROUP).trim();
        String eventDatesString = matcher.group(ADD_ARGS_DATE_EVENT_GROUP);
        String deadineString = matcher.group(ADD_ARGS_DATE_DEADLINE_GROUP);
        
        // Parse accordingly using DateParser and return the right command
        if (deadineString != null && !deadineString.isEmpty()) {
            List<Date> deadlineDates = DateParser.parse(deadineString);
            return prepareDeadlineAdd(args, nameString, deadlineDates);
        } else if (eventDatesString != null && !eventDatesString.isEmpty()) {
            List<Date> eventDates = DateParser.parse(eventDatesString);
            return prepareEventAdd(args, nameString, eventDates);
        } else {
            return prepareFloatAdd(args);
        }
        
    }

    private Command prepareFloatAdd(String name) {
        try {
            return new AddCommand(name);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    /**
     * Prepares a deadline task add command. returns 
     * @param name Deadline task name
     * @param dates List of dates, should be 1 in order to prepare valid add command.
     * @return a valid add deadline or float command or an incorrectCommand
     */
    private Command prepareDeadlineAdd(String fullArgs, String name, List<Date> dates) {
        if (dates == null || dates.size() > 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
        
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs);
        }

        try {
            return generateDeadlineAddCommand(fullArgs, name, dates);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        
    }
    
    /**
     * Generates a deadline add command based on given args.
     * @param fullArgs full argument string
     * @param nameString name to be used for deadline add command.
     * @param dates List of dates to be use to generate deadline.
     * @return a valid deadline add command
     * @throws IllegalValueException
     */
    private AddCommand generateDeadlineAddCommand(String fullArgs, 
            String nameString, List<Date> dates) throws IllegalValueException{
        assert dates.size() == 1;
        
        return new AddCommand(nameString, dates.get(0));
    }
    
    /**
     * Prepares an event add command. Checks that number of dates supplied
     * is more than 2, otherwise returns Incorrect Command.
     * Will generate float or event add command accordingly.
     * @param name Event task name
     * @param dates List of dates, should contain 2 dates: start and end date to be valid.
     * @return a valid add event or float command or an incorrectCommand
     */
    private Command prepareEventAdd(String fullArgs, String name, 
            List<Date> dates) {
        if (dates == null || dates.size() > 2) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
        
        // If no dates are detected, fallback to preparing a float add
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs);
        }
        
        try {
            return generateEventAddCommand(fullArgs, name, dates);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    private AddCommand generateEventAddCommand(String fullArgs, String name, 
            List<Date> dates) throws IllegalValueException{
        assert dates.size() == 1 || dates.size() == 2;
        
        if (dates.size() == 2) {
            return new AddCommand(name, dates.get(0), dates.get(1));
        } else {
            return new AddCommand(name, dates.get(0), dates.get(0));
        }
    }

}
