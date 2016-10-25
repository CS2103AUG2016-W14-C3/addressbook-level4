package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.RemoveCommand;

/**
 * RemoveCommandParser to handle parsing of command arguments.
 * @author Abel
 *
 */
public class RemoveCommandParser extends CommandParser {

    public RemoveCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return RemoveCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return prepareRemove(args);
    }
    
    /**
     * Parses arguments in the context of the remove task command.
     *
     * @param args
     *            full command args string
     * @return the prepared command
     */
    private Command prepareRemove(String args) {
        String argsTrim = args.trim();
        String []s = argsTrim.split(" ");
        for(int i=0; i<s.length; i++)
        {
            Optional<Integer> index = parseIndex(s[i]);
            if (!index.isPresent()) {
                return new IncorrectCommand(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                      RemoveCommand.MESSAGE_USAGE));
            }
        }
        
    	return new RemoveCommand(args);
    }
}
