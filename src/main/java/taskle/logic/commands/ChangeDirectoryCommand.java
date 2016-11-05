package taskle.logic.commands;

import java.io.File;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
import taskle.commons.util.StorageUtil;

//@@author A0140047U
//Change directory of current storage file
public class ChangeDirectoryCommand extends Command {

    public static final String COMMAND_WORD_SHORT = "s";
    public static final String COMMAND_WORD = "save";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reads data from specified file.\n"
            + "Format: " + COMMAND_WORD + " directory_path\n"
            + "Example: " + COMMAND_WORD + " C:" + File.separator + "Users" + File.separator + "John"
            + File.separator + "desktop";

    public static final String MESSAGE_SUCCESS = "Storage Directory has been changed to %1$s";
    
    public static final String MESSAGE_FAILURE = "An error occurred when changing directory.";
    public static final String MESSAGE_FILE_CONFLICT = "Existing file found in requested directory.";
    public static final String MESSAGE_SAME_DIRECTORY = "Requested directory is the same as current.";
    
    private final File file;
    
    public ChangeDirectoryCommand(String directoryPath) {
        this.file = FileUtil.convertToCanonicalPath(new File(directoryPath));
    }
    
    @Override
    public CommandResult execute() {
        try {
            if (isConflict()) {
                return new CommandResult(MESSAGE_FAILURE, false);
            }
            
            model.storeTaskManager(COMMAND_WORD);    
            if (StorageUtil.updateDirectory(file)) {
                return new CommandResult(String.format(MESSAGE_SUCCESS, file.getAbsolutePath()), true);
            } else {
                indicateAttemptToExecuteIncorrectCommand(MESSAGE_FAILURE);
                StorageUtil.resolveConfig();
                model.rollBackTaskManager();
                return new CommandResult(MESSAGE_FAILURE, false);
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
            return new CommandResult(MESSAGE_FAILURE, false);
        }
    }
    
    //Checks if requested directory contains a conflicting file or points to the current directory
    public boolean isConflict() throws DataConversionException {
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        if (config.getTaskManagerFileDirectory().equalsIgnoreCase(file.getAbsolutePath())) {
            indicateAttemptToExecuteIncorrectCommand(MESSAGE_SAME_DIRECTORY);
            return true;
        } else if (new File(file.getAbsolutePath(), config.getTaskManagerFileName()).exists()) {
            indicateAttemptToExecuteIncorrectCommand(MESSAGE_FILE_CONFLICT);
            return true;
        } else {
            return false;
        }
    }
}
