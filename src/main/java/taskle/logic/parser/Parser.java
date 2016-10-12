package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static taskle.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.Command;
import taskle.logic.commands.HelpCommand;
import taskle.logic.commands.IncorrectCommand;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = 
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
        
    private List<CommandParser> commandParsers;

    public Parser() {
        setupParsers();
    }
    
    private void setupParsers() {
        // Generate a list of command parsers here, every new 
        // command added must be added to the commandParsers list here
        commandParsers = new ArrayList<>(
                Arrays.asList(new AddCommandParser(),
                              new RemoveCommandParser(),
                              new EditCommandParser(),
                              new FindCommandParser(),
                              new ListCommandParser(),
                              new HelpCommandParser(),
                              new ClearCommandParser(),
                              new ExitCommandParser()));
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
        for (CommandParser commandParser : commandParsers) {
            if (commandParser.getCommandWord().equals(commandWord)) {
                return commandParser.parseCommand(args);
            }
        }
        
        return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
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

}