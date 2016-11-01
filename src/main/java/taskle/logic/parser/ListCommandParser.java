package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.ListCommand;
//@author A0141780J

/**
 * ListCommandParser class to handle parsing of list commands.
 * @author Abel
 *
 */
public class ListCommandParser extends CommandParser {

    // one or more keywords separated by whitespace
    private static final Pattern LIST_KEYWORDS_ARGS_FORMAT = 
            Pattern.compile("(?:-(?:(?<all>all)|"
                    + "(?<done>done)|"
                    + "(?<overdue>overdue)|"
                    + "(?<pending>pending))(?:\\s|$))+");
    
    public ListCommandParser() {
    }

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(ListCommand.COMMAND_WORD)
                || commandWord.equals(ListCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareList(args);
    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareList(String args) {
        final Matcher matcher = 
                LIST_KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        
        if (!args.isEmpty() && !matcher.matches()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  ListCommand.MESSAGE_USAGE));
        }
        
        if (args.isEmpty()) {
            return new ListCommand(true, false, true);
        }
        
        String done = matcher.group("done");
        String pending = matcher.group("pending");
        String overdue = matcher.group("overdue");
        String all = matcher.group("all");
        
        if (all != null) {
            return new ListCommand(true, true, true);
        }
        
        return new ListCommand(pending != null, done != null, overdue != null);
    }
    
}
