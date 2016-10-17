package taskle.logic.parser;

import taskle.logic.commands.Command;
import taskle.logic.commands.HelpCommand;

/**
 * Help command parser to handle parsing of help commands.
 * @author Abel
 *
 */
public class HelpCommandParser extends CommandParser {

    public HelpCommandParser() {
    }

    @Override
    public String getCommandWord() {
        return HelpCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return new HelpCommand();
    }

}
