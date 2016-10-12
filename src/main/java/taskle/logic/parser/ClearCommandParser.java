package taskle.logic.parser;

import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;

public class ClearCommandParser extends CommandParser {

    public ClearCommandParser() {
        // TODO Auto-generated constructor stub
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
