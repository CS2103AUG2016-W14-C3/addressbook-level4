package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;

import org.junit.Test;

import taskle.logic.commands.OpenFileCommand;

//Test for opening a new file
public class OpenFileCommandTest extends TaskManagerGuiTest {
    
    private static final String TEST_DATA_FOLDER = "src" + File.separator + "test" +
            File.separator + "data" + File.separator + "StorageDirectoryUtilTest" + File.separator;
    
    private static final String INEXISTENT_FILE = " Inexistent.xml";
    private static final String INVALID_FILE = "InvalidFormatTaskManager.xml";
    private static final String VALID_FILE = "ValidFormatTaskManager.xml";
    
    //Open an inexistent file
    @Test
    public void openFile_inexistentFile_incorrectCommand() {
        String command = OpenFileCommand.COMMAND_WORD + INEXISTENT_FILE;
        assertOpenFileInexistentFile(command);
    }
    
    //Open an invalid file
    @Test
    public void openFile_invalidFile_errorMessageShown() {
        String command = OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER + INVALID_FILE;
        assertOpenFileInvalidFile(command);
    }

    private void assertOpenFileInexistentFile(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, OpenFileCommand.MESSAGE_USAGE));
    }
    
    private void assertOpenFileInvalidFile(String command) {
        commandBox.runCommand(command);
        assertResultMessage(OpenFileCommand.MESSAGE_FAILURE);
    }
}
