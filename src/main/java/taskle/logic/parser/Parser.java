package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static taskle.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.commons.exceptions.IllegalValueException;
import taskle.commons.util.StringUtil;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.ExitCommand;
import taskle.logic.commands.FindCommand;
import taskle.logic.commands.HelpCommand;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.ListCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.SelectCommand;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Used for initial separation of command word and args.
     */
    
    // Pattern to group command into command word and arguments string with 
    // a whitespace prefix.
    private static final Pattern BASIC_COMMAND_FORMAT = 
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = 
            Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern TASK_NAME_ARGS_FORMAT = 
            Pattern.compile("(?<targetName>.+)");

    // one or more keywords separated by whitespace
    private static final Pattern FIND_KEYWORDS_ARGS_FORMAT = 
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)");

    // Groups into name (all words until first by|from), 
    // dateFrom (all words after from containing from at the start)
    // or dateBy (all words after by containing by at the start).
    private static final Pattern ADD_ARGS_FORMAT = 
            Pattern.compile("(?<name>.+?(?=\\sby|\\sfrom|$))"
                    + "(?<dateFrom>(?=\\sfrom).*)*"
                    + "(?<dateBy>(?=\\sby).*)*");
    
    private static final int EDIT_NUM_INPUT = 2;

    public Parser() {
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        return prepareCommand(commandWord, arguments);
    }
    
    /**
     * Prepares commmand based on command word and arguments.
     * @param commandWord command word from user input.
     * @param args arguments after command word from user input.
     * @return The corresponding command to the command word after parsing. 
     */
    private Command prepareCommand(String commandWord, String args) {
        switch (commandWord) {
        case EditCommand.COMMAND_WORD:
            return prepareEdit(args);
        case AddCommand.COMMAND_WORD:
            return prepareAdd(args);
        case SelectCommand.COMMAND_WORD:
            return prepareSelect(args);
        case RemoveCommand.COMMAND_WORD:
            return prepareDelete(args);
        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();
        case FindCommand.COMMAND_WORD:
            return prepareFind(args);
        case ListCommand.COMMAND_WORD:
            return new ListCommand();
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();
        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        default:
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
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
        
        String name = matcher.group("name");
        String eventDate = matcher.group("dateFrom");
        String deadlineDate = matcher.group("dateBy");
        if (deadlineDate != null && !deadlineDate.isEmpty()) {
            List<Date> dates = DateParser.parse(deadlineDate);
            return prepareDeadlineAdd(name, dates);
        } else if (eventDate != null && !eventDate.isEmpty()) {
            List<Date> dates = DateParser.parse(eventDate);
            return prepareEventAdd(name, dates);
        } else {
            return prepareFloatAdd(name);
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
     * Prepares an event add command. Checks that number of dates supplied
     * is 2, otherwise returns Incorrect Command.
     * @param name Event task name
     * @param dates List of dates, should contain 2 dates: start and end date to be valid.
     * @return a valid event task add command.
     */
    private Command prepareEventAdd(String name, List<Date> dates) {
        if (dates == null || dates.size() != 2) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }
        
        try {
            return new AddCommand(name, dates.get(0), dates.get(1));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    /**
     * Prepares a deadline task add command. Checks that number of dates
     * supplied is 1, otherwise returns Incorrect Command.
     * @param name Deadline task name
     * @param dates List of dates, should be 1 in order to prepare valid add command.
     * @return a valid deadline task add command.
     */
    private Command prepareDeadlineAdd(String name, List<Date> dates) {
        if (dates == null || dates.size() != 1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                    AddCommand.MESSAGE_USAGE));
        }

        try {
            return new AddCommand(name, dates.get(0));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        
    }

    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) 
            throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = 
                Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Parses arguments in the context of the remove task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<Integer> index = parseIndex(args);
        if (!index.isPresent()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  RemoveCommand.MESSAGE_USAGE));
        }
        return new RemoveCommand(index.get());
    }

    /**
     * Parses arguments in the context of the edit task command
     * 
     * @param args
     * @return the prepared command with the task number and the new task name
     */
    private Command prepareEdit(String args) {
        args = args.trim();
        int endIndex = args.indexOf(" ");
        if (endIndex == -1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  EditCommand.MESSAGE_USAGE));
        }
        
        String indexValue = args.substring(0, endIndex);
        String newName = args.substring(endIndex).trim();
        Optional<Integer> index = parseIndex(indexValue);
        Optional<String> name = parseName(newName);
        if (!index.isPresent() || !name.isPresent()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  EditCommand.MESSAGE_USAGE));
        }
        
        try {
            return new EditCommand(index.get(), name.get());
        } catch (IllegalValueException e) {
            return new IncorrectCommand(e.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if (!index.isPresent()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  SelectCommand.MESSAGE_USAGE));
        }
        
        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned
     * integer is given as the index. Returns an {@code Optional.empty()}
     * otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        
        String index = matcher.group("targetIndex");
        if (!StringUtil.isUnsignedInteger(index)) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Returns the specified name in the command
     * 
     * @param command
     * @return
     */
    private Optional<String> parseName(String command) {
        final Matcher matcher = TASK_NAME_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        
        String name = matcher.group("targetName");
        return Optional.of(name);
    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = FIND_KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  FindCommand.MESSAGE_USAGE));
        }
        
        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

}