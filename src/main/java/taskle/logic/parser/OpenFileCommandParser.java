package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;

import taskle.commons.util.FileUtil;
import taskle.logic.commands.Command;
import taskle.logic.commands.IncorrectCommand;
import taskle.logic.commands.OpenFileCommand;

//@@author A0140047U
//OpenFileParser class to handle parsing of open file commands
public class OpenFileCommandParser extends CommandParser {

    @Override
    public Command parseCommand(String args) {
        return parseOpenFile(args.trim());
    }
    
    private Command parseOpenFile(String filePath) {
        if (FileUtil.isFileExists(new File(filePath))) {
            return new OpenFileCommand(filePath);
        } else {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  OpenFileCommand.MESSAGE_USAGE));
        }
    }

    @Override
    public boolean canParse(String commandWord) {
        return commandWord.equals(OpenFileCommand.COMMAND_WORD)
               || commandWord.equals(OpenFileCommand.COMMAND_WORD_SHORT);
    }

}
