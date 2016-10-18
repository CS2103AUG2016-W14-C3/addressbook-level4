package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.ExitCommand;

/**
 * ExitCommandParser class to handle parsing of exit commands.
 * @author Abel
 *
 */
public class ExitCommandParser extends CommandParser {

    public ExitCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return ExitCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new ExitCommand();
    }

}
