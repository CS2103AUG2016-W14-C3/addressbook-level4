package guitests;

import static org.junit.Assert.assertTrue;
import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
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
    
    //Change to a valid directory
    @Test
    public void changeDirectory_validFormat_directoryChanged() throws DataConversionException, IOException {
        String command = ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER;
        assertChangeDirectorySuccess(command);
    }
    
    private void assertChangeDirectorySuccess(String command) throws DataConversionException, IOException {
        commandBox.runCommand(command);
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
    }
    
    private void assertChangeDirectoryInvalidDirectory(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ChangeDirectoryCommand.MESSAGE_USAGE));
    }
}
