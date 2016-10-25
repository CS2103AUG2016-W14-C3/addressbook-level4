package taskle.logic.parser;

import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;
//@author A0141780J

/**
 * Clear command parser class to handle parsing of clear commands.
 * @author Abel
 *
 */
public class ClearCommandParser extends CommandParser {

    public ClearCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return ClearCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new ClearCommand();
    }

}
