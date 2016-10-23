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
    private static final String ADD_ARGS_DATE = "date";
    private static final String ADD_ARGS_REMINDER = "remindDateTime";
    
    // Groups into name (all words until last by|from), 
    // dateFrom (all words after from containing from at the start)
    // or dateBy (all words after by containing by at the start).
    private static final Pattern ADD_ARGS_FORMAT = 
            Pattern.compile("(?<name>.+\\s(?=by|from)|.+$)"
                    + "(?<dateEvent>(?=from).*)*"
                    + "(?<dateDeadline>(?=by).*)*");
    
    //group the date and the reminder date separately
    private static final Pattern GET_REMINDER_FORMAT = 
            Pattern.compile("(?<date>.+\\s(?=remind)|.+$)" + "(?<remindDateTime>(?=remind).*)*");
    
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
        String deadlineString = matcher.group(ADD_ARGS_DATE_DEADLINE_GROUP);
        
        // Parse accordingly using DateParser and return the right command
        // Get the reminder date out if it exists and return the right command
        if (deadlineString != null && !deadlineString.isEmpty()) {
            String[] dates = parseReminder(deadlineString);
            String dateString = dates[0];
            String reminderString = dates[1];
            List<Date> deadlineDates = DateParser.parse(dateString);

            if(reminderString != null) {
                List<Date> reminderDate = DateParser.parse(reminderString);
                return prepareDeadlineAdd(args, nameString, deadlineDates, reminderDate);
            } else {
                return prepareDeadlineAdd(args, nameString, deadlineDates);
            }            
        } else if (eventDatesString != null && !eventDatesString.isEmpty()) {
            String[] dates = parseReminder(eventDatesString);
            String dateString = dates[0];
            String reminderString = dates[1];
            List<Date> eventDates = DateParser.parse(dateString);
            
            if(reminderString != null) {
                List<Date> reminderDate = DateParser.parse(reminderString);
                return prepareEventAdd(args, nameString, eventDates, reminderDate);
            } else {
                return prepareEventAdd(args, nameString, eventDates);
            }            

        } else {
            String[] dates = parseReminder(args);
            String task = dates[0];
            String reminderString = dates[1];
            
            if(reminderString != null) {
                List<Date> reminderDate = DateParser.parse(reminderString);
                return prepareFloatAdd(task, reminderDate);
            } else {
                return prepareFloatAdd(task);
            }    
        }
        
    }
    
    /**
     * Method to separate the reminder string and the date string
     * and return it as an array with the first element as the date and
     * the second element as the reminder date.
     * @param dateString
     * @return
     */
    private String[] parseReminder(String date) {
        final Matcher matcherReminder = GET_REMINDER_FORMAT.matcher(date);
        matcherReminder.matches();
        String dateString = matcherReminder.group(ADD_ARGS_DATE);
        String reminderString = matcherReminder.group(ADD_ARGS_REMINDER);
        return new String[] {dateString, reminderString};
    }

    private Command prepareFloatAdd(String name) {
        try {
            return new AddCommand(name);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    /**
     * Prepares a float task with reminder
     * @param name
     * @param remindDate should be 1
     * @return a valid float command
     */
    private Command prepareFloatAdd(String name, List<Date> remindDate) {
        if (remindDate == null || remindDate.size() != 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }        

        try {
            return new AddCommand(name, remindDate);
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
        Command errorCheckingCommand = errorCheckingDeadline(fullArgs, dates);
        
        if(errorCheckingCommand != null) {
            return errorCheckingCommand;
        }
        
        try {
            return generateDeadlineAddCommand(fullArgs, name, dates);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        
    }
    
    
    /**
     * Prepares a deadline task add command with reminder date. returns 
     * @param name Deadline task name
     * @param dates List of dates, should be 1 in order to prepare valid add command.
     * @param remindDate List of remind dates. Should be 1 only.
     * @return a valid add deadline or float command or an incorrectCommand
     */
    private Command prepareDeadlineAdd(String fullArgs, String name, List<Date> dates, List<Date> remindDate) {
        Command errorCheckingCommand = errorCheckingDeadline(fullArgs, dates);
        
        if(errorCheckingCommand != null) {
            return errorCheckingCommand;
        }
        
        if (remindDate == null || remindDate.size() != 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }        

        try {
            return generateDeadlineAddCommand(fullArgs, name, dates, remindDate);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        
    }
    
    private Command errorCheckingDeadline(String fullArgs, List<Date> dates) {
        if (dates == null || dates.size() > 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
        
        // If no dates are detected, fallback to preparing a float add
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs);
        }
        return null;
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
     * Generates a deadline add command with reminder date
     * @param fullArgs full argument string
     * @param nameString name to be used for deadline add command
     * @param dates List of dates to be used to generate deadline. Should be 1.
     * @param remindDate List of dates to be used for reminder date. Should be 1.
     * @return a valid deadline add command with reminder date
     * @throws IllegalValueException
     */
    private AddCommand generateDeadlineAddCommand(String fullArgs, 
            String nameString, List<Date> dates, List<Date> remindDate) throws IllegalValueException{
        assert remindDate.size() == 1;
        assert dates.size() == 1;

        return new AddCommand(nameString, dates.get(0), remindDate);
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
        Command errorCheckingCommand = errorCheckingEvent(fullArgs, dates);
        
        if(errorCheckingCommand != null) {
            return errorCheckingCommand;
        }
        
        try {
            return generateEventAddCommand(fullArgs, name, dates);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    /**
     * Prepares an event add command with reminder. Checks that number of dates supplied
     * is more than 2, otherwise returns Incorrect Command.
     * Will generate float or event add command accordingly.
     * @param name Event task name
     * @param dates List of dates, should contain 2 dates: start and end date to be valid.
     * @param remindDate List of reminder dates. Should contain 1 date only.
     * @return a valid add event or float command or an incorrectCommand
     */
    private Command prepareEventAdd(String fullArgs, String name, 
            List<Date> dates, List<Date> remindDate) {
        Command errorCheckingCommand = errorCheckingEvent(fullArgs, dates);
        
        if(errorCheckingCommand != null) {
            return errorCheckingCommand;
        }
        
        if (remindDate == null || remindDate.size() != 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
       
        try {
            return generateEventAddCommand(fullArgs, name, dates, remindDate);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    private Command errorCheckingEvent(String fullArgs, List<Date> dates) {
        if (dates == null || dates.size() > 2) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
        
        // If no dates are detected, fallback to preparing a float add
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs);
        }
        return null;
    }
    
    /**
     * Generates a event add command
     * @param fullArgs full argument string
     * @param name name to be used for deadline add command
     * @param dates List of dates to be used to generate deadline. Should be 1.
     * @return a valid event add command with reminder date
     * @throws IllegalValueException
     */
    private AddCommand generateEventAddCommand(String fullArgs, String name, 
            List<Date> dates) throws IllegalValueException{
        assert dates.size() == 1 || dates.size() == 2;
        
        if (dates.size() == 2) {
            return new AddCommand(name, dates.get(0), dates.get(1));
        } else {
            return new AddCommand(name, dates.get(0), dates.get(0));
        }
    }
    
    /**
     * Generates a event add command with reminder date
     * @param fullArgs full argument string
     * @param name name to be used for deadline add command
     * @param dates List of dates to be used to generate deadline. Should be 1.
     * @param remindDate List of dates to be used for reminder date. Should be 1.
     * @return a valid event add command with reminder date
     * @throws IllegalValueException
     */
    private AddCommand generateEventAddCommand(String fullArgs, String name, 
            List<Date> dates, List<Date> remindDate) throws IllegalValueException{
        assert dates.size() == 1 || dates.size() == 2;
        assert remindDate.size() == 1;
        
        if (dates.size() == 2) {
            return new AddCommand(name, dates.get(0), dates.get(1), remindDate);
        } else {
            return new AddCommand(name, dates.get(0), dates.get(0), remindDate);
        }
    }
    
}
