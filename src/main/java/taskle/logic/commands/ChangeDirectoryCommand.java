package taskle.logic.commands;

import java.io.File;

import taskle.commons.core.Messages;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.StorageUtil;

//@@author A0140047U
//Change directory of current storage file
public class ChangeDirectoryCommand extends Command {

    public static final String COMMAND_WORD = "changedirectory";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reads data from specified file.\n"
            + "Format: add directory\n"
            + "Example: " + COMMAND_WORD + " C:" + File.separator + "Users" + File.separator + "John"
            + File.separator + "desktop";

    public static final String MESSAGE_SUCCESS = "Storage Directory has been changed to %1$s";
    
    public static final String MESSAGE_FAILURE = "An error occurred when changing directory.";
    
    private final File file;
    
    public ChangeDirectoryCommand(String directoryPath) {
        file = new File(directoryPath);
    }
    
    @Override
    public CommandResult execute() {
        try {
            StorageUtil.storeConfig(true);
            if (StorageUtil.updateDirectory(file)) {
                return new CommandResult(String.format(MESSAGE_SUCCESS, file.getAbsolutePath()), true);
            } else {
                indicateAttemptToExecuteIncorrectCommand(MESSAGE_FAILURE);
                return new CommandResult(MESSAGE_FAILURE, false);
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
            return new CommandResult(MESSAGE_FAILURE, false);
        }
       
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}
