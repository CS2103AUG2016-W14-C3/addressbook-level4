package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;

import org.junit.Test;

import taskle.logic.commands.ChangeDirectoryCommand;

//Tests for change in directory
public class ChangeDirectoryCommandTest extends TaskManagerGuiTest {
    
    private static final String TEST_DATA_FOLDER = "src" + File.separator + "test" +
            File.separator + "data" + File.separator + "StorageDirectoryUtilTest";
    
    //Change to an invalid directory
    @Test
    public void changeDirectory_invalidDirectory_incorrectCommand() {
        String command = ChangeDirectoryCommand.COMMAND_WORD + " /i/n/v/a/l/i/d";
        assertChangeDirectoryInvalidDirectory(command);
    }
    
    private void assertChangeDirectoryInvalidDirectory(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ChangeDirectoryCommand.MESSAGE_USAGE));
    }
}
