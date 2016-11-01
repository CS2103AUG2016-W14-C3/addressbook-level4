package taskle.logic.commands;

import java.io.File;

import taskle.commons.util.StorageUtil;

//@@author A0140047U
//Opens data from specified file
public class OpenFileCommand extends Command {
    
    public static final String COMMAND_WORD = "openfile";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reads data from specified file.\n"
            + "Format: add file_path\n"
            + "Example: " + COMMAND_WORD + " C:" + File.separator + "Users" + File.separator + "John"
            + File.separator + "desktop" + File.separator + "taskle.xml";

    public static final String MESSAGE_SUCCESS = "Storage File has been changed.";
    
    public static final String MESSAGE_FAILURE = "Invalid file format detected. Unable to open file";
    
    private final File file;
    
    public OpenFileCommand(String filePath) {
        this.file = new File(filePath);
    }
    
    @Override
    public CommandResult execute() {
        if (StorageUtil.updateFile(file)) {
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            return new CommandResult(MESSAGE_FAILURE);
        }
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
