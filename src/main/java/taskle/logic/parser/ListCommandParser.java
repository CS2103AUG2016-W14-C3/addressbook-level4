package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.ListCommand;

/**
 * ListCommandParser class to handle parsing of list commands.
 * @author Abel
 *
 */
public class ListCommandParser extends CommandParser {

    public ListCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return ListCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new ListCommand();
    }

}
