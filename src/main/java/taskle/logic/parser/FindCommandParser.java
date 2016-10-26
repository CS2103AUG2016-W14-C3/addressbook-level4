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
import taskle.logic.commands.ListCommand;
//@author A0141780J
/**
 * FindCommandParser class to handle parsing of Find Commands.
 * @author Abel
 *
 */
public class FindCommandParser extends CommandParser {

    // one or more keywords separated by whitespace
    private static final Pattern FIND_KEYWORDS_ARG_FORMAT = 
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)");
    
    // one or more keywords separated by whitespace
    private static final Pattern FIND_STATUS_ARGS_FORMAT = 
            Pattern.compile("(?:-(?:(?<all>all)|"
                    + "(?<done>done)|"
                    + "(?<overdue>overdue)|"
                    + "(?<pending>pending))(?:\\s|$))+");
    
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
        String[] argParams = args.trim().split("-", 2);
        if (argParams[0].isEmpty()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  FindCommand.MESSAGE_USAGE));
        }
        
        String keywordsParam = argParams[0].trim();
        String statusesParam = "";
        if (argParams.length == 2) {
            statusesParam = "-" + argParams[1];
        }
        
        Matcher keywordMatcher = FIND_KEYWORDS_ARG_FORMAT.matcher(keywordsParam);
        Matcher statusMatcher = FIND_STATUS_ARGS_FORMAT.matcher(statusesParam);
        
        if (!keywordsParam.isEmpty() && !keywordMatcher.matches() 
            || !statusesParam.isEmpty() && !statusMatcher.matches()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  FindCommand.MESSAGE_USAGE));
        }
        
        // keywords delimited by whitespace
        final String[] keywords = keywordMatcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        
        if (statusesParam.isEmpty()) {
            return new FindCommand(keywordSet);
        }
        
        return getStatusFindCommand(statusMatcher, keywordSet);
    }
    
    /**
     * Parses and returns a Find Command base on filters set in status matcher
     * and the keywords as defined in keywordSet.
     * @param statusMatcher status matcher that contains the status flags.
     * @param keywordSet set of keywords to find
     * @return
     */
    private FindCommand getStatusFindCommand(Matcher statusMatcher, Set<String> keywordSet) {
        String done = statusMatcher.group("done");
        String pending = statusMatcher.group("pending");
        String overdue = statusMatcher.group("overdue");
        String all = statusMatcher.group("all");
        
        if (all != null) {
            return new FindCommand(keywordSet, true, true, true);
        }
        
        return new FindCommand(
                keywordSet, pending != null, 
                done != null, overdue != null);
    }

}
