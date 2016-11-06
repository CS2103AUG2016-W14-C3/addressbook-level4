package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.commons.core.Messages;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;

//@author A0141780J
/**
 * AddCommandParser class to handle parsing of add commands.
 */
public class AddCommandParser extends CommandParser {

    private static final String ADD_ARGS_NAME_GROUP = "name";
    private static final String ADD_ARGS_DATE_DEADLINE_GROUP = "dateDeadline";
    private static final String ADD_ARGS_DATE_EVENT_GROUP = "dateEvent";
    private static final String ADD_ARGS_DATE_REMINDER_GROUP = "dateRemind";

    public AddCommandParser() {
    }
    
    /**
     * Regex pattern that groups into name (all words until last by|from),
     * dateFrom (all words after from containing from at the start) or dateBy
     * (all words after by containing by at the start). The reminder date is
     * also captured in dateRemind.
     */
    private static final Pattern ADD_ARGS_FORMAT = Pattern.compile(
            "(?<name>(?:.+\\s(?=by|from))|.+?(?=remind)|.+$|)" 
            + "(?<dateEvent>(?=from)(?:.+\\s(?=remind)|.+$))*"
            + "(?<dateDeadline>(?=by)(?:.+\\s(?=remind)|.+$))*" 
            + "(?<dateRemind>(?=remind).*)*");

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(AddCommand.COMMAND_WORD) 
               || commandWord.equals(AddCommand.COMMAND_WORD_SHORT);
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
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        // Get respective name, date Strings and reminder date
        String nameString = matcher.group(ADD_ARGS_NAME_GROUP).trim();
        String eventDatesString = matcher.group(ADD_ARGS_DATE_EVENT_GROUP);
        String deadlineString = matcher.group(ADD_ARGS_DATE_DEADLINE_GROUP);

        // Check if name argument exists
        if (nameString == null || nameString.isEmpty()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  AddCommand.MESSAGE_USAGE));
        }
        
        // Check if reminder argument is valid
        String remindDateString = matcher.group(ADD_ARGS_DATE_REMINDER_GROUP);
        Date remindDate = DateParser.parseRemindDate(remindDateString);
        // If reminder is stated but has no date
        if (remindDateString != null && !remindDateString.isEmpty()
            && (eventDatesString != null || deadlineString != null) 
            && remindDate == null) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  AddCommand.MESSAGE_USAGE));
        }

        return getCorrectCommand(args, nameString, eventDatesString, deadlineString, remindDate);
    }

    /**
     * Gets the correct command based on parameters supplied.
     * 
     * @param args full argument string
     * @param nameString name argument string
     * @param eventDatesString event dates argument string
     * @param deadlineString deadline dates argument string
     * @param remindDate reminder dates argument string
     * @return The correct command based on given arguments.
     */
    private Command getCorrectCommand(
            String args, String nameString, String eventDatesString, 
            String deadlineString, Date remindDate) {
        // Parse accordingly using DateParser and return the right command
        if (deadlineString != null && !deadlineString.isEmpty()) {
            List<Date> deadlineDates = DateParser.parse(deadlineString);
            return prepareDeadlineAdd(args, nameString, remindDate, deadlineDates);
        } else if (eventDatesString != null && !eventDatesString.isEmpty()) {
            List<Date> eventDates = DateParser.parse(eventDatesString);
            return prepareEventAdd(args, nameString, remindDate, eventDates);
        } else {
            return prepareFloatAdd(args, nameString, remindDate);
        }
    }

    /**
     * Prepares a float task with reminder.
     * 
     * @param args Full string for the argument.
     * @param name Name as extracted from argument.
     * @param remindDate Reminder date as extracted. Nullable.
     * @return a valid float Add Command.
     */
    private Command prepareFloatAdd(String args, String name, Date remindDate) {
        if (remindDate == null) {
            return new AddCommand(args);
        }

        return new AddCommand(name, null, null, remindDate);
    }

    /**
     * Prepares a deadline task add command with reminder date. returns
     * 
     * @param name Deadline task name
     * @param dates List of dates, should be 1 in order to prepare valid add command.
     * @param remindDate List of remind dates. Should be 1 only.
     * @return a valid add deadline or float command or an incorrectCommand
     */
    private Command prepareDeadlineAdd(String fullArgs, String name, Date remindDate, List<Date> dates) {
        // Check if number of dates given are valid
        if (dates == null || dates.size() > 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  AddCommand.MESSAGE_USAGE));
        }
        
        // No dates means we should fallback to adding a float
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs, name, remindDate);
        }

        // Check if reminder was specified but reminder is invalid
        if(remindDate != null && checkInvalidReminderDate(remindDate, dates)) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE));
        }

        return generateDeadlineAddCommand(name, dates, remindDate);

    }

    /**
     * Prepares an event add command with reminder. Checks that number of dates
     * supplied is more than 2, otherwise returns Incorrect Command. Will
     * generate float or event add command accordingly.
     * 
     * @param name Event task name
     * @param dates List of dates, should contain 2 dates: start and end date to be valid.
     * @param remindDate List of reminder dates. Should contain 1 date only.
     * @return a valid add event or float command or an incorrectCommand
     */
    private Command prepareEventAdd(
            String fullArgs, String name, Date remindDate, List<Date> dates) {
        // Check if number of dates given are valid
        if (dates == null || dates.size() > 2) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  AddCommand.MESSAGE_USAGE));
        }

        // No dates given, fallback to preparing a float add
        if (dates.size() == 0) {
            return prepareFloatAdd(fullArgs, name, remindDate);
        }
        
        // Check if reminder was specified but reminder is invalid
        if(remindDate != null && checkInvalidReminderDate(remindDate, dates)) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE));
        }
        
        return generateEventAddCommand(name, dates, remindDate);
    }
    
    //@@author A0139402M
    /**
     * Method to check if the reminder date is after the end date of the task 
     * 
     * @param remindDate given remind date
     * @param dates list of dates
     * @return
     */
    private boolean checkInvalidReminderDate(Date remindDate, List<Date> dates) {
        if (remindDate.after(dates.get(dates.size() - 1))) {
            return true;
        }
        
        return false;
    }
    
    //@@author A0141780J
    /**
     * Generates a deadline add command with reminder date
     * 
     * @param nameString name to be used for deadline add command
     * @param dates List of dates to be used to generate deadline. Should be 1.
     * @param remindDate reminder date. Nullable.
     * @return a valid deadline add command with reminder date
     */
    private AddCommand generateDeadlineAddCommand(
            String nameString, List<Date> dates, Date remindDate) {
        assert dates.size() == 1;
        return new AddCommand(nameString, null, dates.get(0), remindDate);
    }

    /**
     * Generates a event add command with reminder date
     * 
     * @param fullArgs full argument string
     * @param name name to be used for deadline add command
     * @param dates List of dates to be used to generate deadline. Should be 1.
     * @param remindDate List of dates to be used for reminder date. Should be 1.
     * @return a valid event add command with reminder date
     */
    private AddCommand generateEventAddCommand(
            String name, List<Date> dates, Date remindDate) {
        assert dates.size() == 1 || dates.size() == 2;

        // There are either 2 or 1 dates given, generate accordingly
        if (dates.size() == 2) {
            return new AddCommand(name, dates.get(0), dates.get(1), remindDate);
        } else {
            return new AddCommand(name, dates.get(0), dates.get(0), remindDate);
        }
    }

}
