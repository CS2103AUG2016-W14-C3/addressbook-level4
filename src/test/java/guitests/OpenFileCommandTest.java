package guitests;

import static org.junit.Assert.assertEquals;
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
import taskle.logic.commands.OpenFileCommand;

//@@author A0140047U
//Test for opening a new file
public class OpenFileCommandTest extends TaskManagerGuiTest {
    
    private static final String TEST_DATA_FOLDER = "src" + File.separator + "test" +
            File.separator + "data" + File.separator + "StorageDirectoryUtilTest" + File.separator;
    
    private static final String INEXISTENT_FILE = " Inexistent.xml";
    private static final String INVALID_FILE = "InvalidFormatTaskManager.xml";
    private static final String VALID_FILE = "ValidFormatTaskManager.xml";
    
    private Config config;
    private String taskManagerDirectory;
    private String taskManagerFileName;
    private String taskManagerFilePath;
    
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
    
    //Open the same file
    @Test
    public void openFile_sameFile_messageShown() {
        String command = OpenFileCommand.COMMAND_WORD + " " + taskManagerFilePath;
        assertOpenSameFile(command);
    }
    
    //Open a valid file
    @Test
    public void openFile_validFormat_FileOpened() throws DataConversionException {
        String command = OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER + VALID_FILE;
        assertOpenFileSuccess(command);
    }
    
    private void assertOpenFileSuccess(String command) throws DataConversionException {
        commandBox.runCommand(command);
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
        assertEquals(config.getTaskManagerFileName(), VALID_FILE);
    }
    
    private void assertOpenFileInexistentFile(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, OpenFileCommand.MESSAGE_USAGE));
    }
    
    private void assertOpenFileInvalidFile(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(OpenFileCommand.MESSAGE_INVALID_FILE_FORMAT);
    }
    
    private void assertOpenSameFile(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(OpenFileCommand.MESSAGE_SAME_FILE);
    }
    
    //Stores original taskManager directory and file name
    @Before
    public void setUp() throws DataConversionException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        taskManagerDirectory = config.getTaskManagerFileDirectory();
        taskManagerFileName = config.getTaskManagerFileName();
        taskManagerFilePath = config.getTaskManagerFilePath();
    }
    
    //Restores original taskManager directory and file name
    @After
    public void tearDown() throws IOException {
        config.setTaskManagerFileDirectory(taskManagerDirectory);
        config.setTaskManagerFileName(taskManagerFileName);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
    }
}
