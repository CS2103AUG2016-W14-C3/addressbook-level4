package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.logic.commands.Command;
import taskle.logic.commands.FindCommand;
import taskle.logic.commands.IncorrectCommand;

public class FindCommandParser extends CommandParser {

 // one or more keywords separated by whitespace
    private static final Pattern FIND_KEYWORDS_ARGS_FORMAT = 
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)");
    
    public FindCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return FindCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return prepareFind(args);
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
