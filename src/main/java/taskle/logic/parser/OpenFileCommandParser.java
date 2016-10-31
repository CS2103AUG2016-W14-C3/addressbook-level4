package taskle.logic.parser;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.OpenFileCommand;

/**
 * OpenFileParser class to handle parsing of open file commands
 */
public class OpenFileCommandParser extends CommandParser {

    @Override
    public String getCommandWord() {
        return OpenFileCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return parseOpenFile(args);
    }
    
    private Command parseOpenFile(String filePath) {
        try {
            return new OpenFileCommand(filePath);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

}
