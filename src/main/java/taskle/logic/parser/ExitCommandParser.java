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
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(ExitCommand.COMMAND_WORD);
    }

    @Override
    public Command parseCommand(String args) {
        return new ExitCommand();
    }

}
