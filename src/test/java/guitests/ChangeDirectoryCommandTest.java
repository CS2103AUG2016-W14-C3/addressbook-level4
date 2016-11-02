package guitests;

import static org.junit.Assert.assertTrue;
import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.UndoCommand;

//@@author A0140047U
//Tests for change in directory
public class ChangeDirectoryCommandTest extends TaskManagerGuiTest {
    
    private static final String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest/");
    private static final String TEST_DATA_TEMP_FOLDER = TEST_DATA_FOLDER + "Temp";
    private static final String TEST_DATA_FILE_VALID_NAME = "ValidFormatTaskManager.xml";
    private static final String TEST_DATA_FILE_EXISTING_NAME = "ExistingTaskManager.xml";
    
    private Config config;
    private File tempDirectory;
    private String taskManagerDirectory;
    private String taskManagerFileName;
    
    //Change to an invalid directory
    @Test
    public void changeDirectory_invalidDirectory_incorrectCommand() {
        String command = ChangeDirectoryCommand.COMMAND_WORD + " /i/n/v/a/l/i/d";
        assertChangeDirectoryIncorrectCommand(command);
    }
    
    //Change to a valid directory
    @Test
    public void changeDirectory_validFormat_directoryChanged() throws DataConversionException, IOException {   
        String command = ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_TEMP_FOLDER;
        assertChangeDirectorySuccess(command);
    }
    
    private void assertChangeDirectorySuccess(String command) throws DataConversionException, IOException {
        commandBox.runCommand(command);
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
        restoreStorage();
    }
    
    private void assertChangeDirectoryIncorrectCommand(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ChangeDirectoryCommand.MESSAGE_USAGE));
    }
    
    private void assertChangeDirectoryConflict(String command, String message) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(message);
    }
    
    
    //Undo change in taskManager directory
    public void restoreStorage() throws IOException {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
    }
}
