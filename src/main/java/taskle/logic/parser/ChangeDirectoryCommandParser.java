package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;

import taskle.commons.util.FileUtil;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;

//ChangeDirectoryParser class to handle parsing of change directory commands
public class ChangeDirectoryCommandParser extends CommandParser {

    @Override
    public String getCommandWord() {
        return ChangeDirectoryCommand.COMMAND_WORD;
    }

    @Override
    public Command parseCommand(String args) {
        return parseChangeDirectory(args.trim());
    }
    
    /**
     * Checks if argument is a directory and the directory exists
     * @param directory path of the directory
     * @return ChangeDirectoryCommand if directory is valid, IncorrectCommand otherwise
     */
    public Command parseChangeDirectory(String directory) {
        if (FileUtil.isDirectoryExists(new File(directory))) {
            return new ChangeDirectoryCommand(directory);
        } else {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  ChangeDirectoryCommand.MESSAGE_USAGE));
        }
    }

}
