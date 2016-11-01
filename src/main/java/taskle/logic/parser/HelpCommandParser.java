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
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(HelpCommand.COMMAND_WORD)
               || commandWord.equals(HelpCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return new HelpCommand();
    }

}
