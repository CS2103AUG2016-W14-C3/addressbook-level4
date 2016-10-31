package taskle.logic.commands;

import java.io.File;

/**
 * Opens data from specified file
 */
public class OpenFileCommand extends Command {
    
    public static final String COMMAND_WORD = "openfile";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reads data from specified file.\n"
            + "Format: add file_path "
            + "Example: " + COMMAND_WORD + " C:" + File.separator + "Users" + File.separator + "John"
            + File.separator + "desktop" + File.separator + "taskle.xml";

    public static final String MESSAGE_SUCCESS = "Reading from File: %1$s";

    @Override
    public CommandResult execute() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCommandWord() {
        // TODO Auto-generated method stub
        return null;
    }
}
