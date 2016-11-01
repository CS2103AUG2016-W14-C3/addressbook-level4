package taskle.logic.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taskle.commons.util.StringUtil;
import taskle.logic.commands.Command;
//@author A0141780J
/**
 * Base abstract class for all command parsers.
 * All new commands added must extend this class 
 * if you want to add a new command.
 * @author Abel
 *
 */
public abstract class CommandParser {
    
    private static final Pattern TASK_NAME_ARGS_FORMAT = 
            Pattern.compile("(?<targetName>.+)");
    
    private static final Pattern TASK_INDEX_ARGS_FORMAT = 
            Pattern.compile("(?<targetIndex>.+)");
    
    public abstract boolean canParse(String commandWord);
    
    public abstract Command parseCommand(String args);
    
    /**
     * Returns the specified index in the {@code command} IF a positive unsigned
     * integer is given as the index. Returns an {@code Optional.empty()}
     * otherwise.
     */
    protected Optional<Integer> parseIndex(String command) {
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
    protected Optional<String> parseName(String command) {
        final Matcher matcher = TASK_NAME_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        
        String name = matcher.group("targetName");
        return Optional.of(name);
    }
    
}
