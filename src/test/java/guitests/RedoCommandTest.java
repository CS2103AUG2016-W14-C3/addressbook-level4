package guitests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
import taskle.commons.util.StorageUtil;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.OpenFileCommand;
import taskle.logic.commands.RedoCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

//@@author A0140047U
public class RedoCommandTest extends TaskManagerGuiTest {
    
    private static String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest/");
    private static String TEST_DATA_FOLDER_TEMP = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest/temp");
    private static String TEST_DATA_FILE_NAME = "ValidFormatTaskManager.xml";
    private static String TEST_DATA_FILE = TEST_DATA_FOLDER + TEST_DATA_FILE_NAME;
    private static final String INVALID_CONFIG = FileUtil.getPath("src/test/data/ConfigUtilTest/NotJasonFormatConfig.json");
    private static final String TEMP_CONFIG = "temp.json";
    
    private Config config;
    private String taskManagerDirectory;
    private String taskManagerFileName;
    
    //Redo when no action has been undone
    @Test
    public void redo_emptyHistory_messageDisplayed() {
        StorageUtil.clearHistory();
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertUnsuccessfulMessage(RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }
    
    //Redo after undo of mutating command    
    @Test
    public void redo_afterUndoCommand_undoRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        currentList = TestUtil.addTasksToList(currentList, td.helpFriend);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        assertRedoSuccess(RedoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Redo after mutating command, should show "Nothing to Redo" message
    @Test
    public void redo_afterMutatingCommand_messageDisplayed() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(RemoveCommand.COMMAND_WORD + " 1");
        currentList = TestUtil.removeTaskFromList(currentList, 1);
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertUnsuccessfulMessage(RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }
    
    //Redo after undo of storage directory change
    @Test
    public void redo_changeDirectory_directoryChanged() {
        try {
            commandBox.runCommand(ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER_TEMP);
            commandBox.runCommand(UndoCommand.COMMAND_WORD);
            assertRedoDirectorySuccess(RedoCommand.MESSAGE_SUCCESS);
            restoreStorage();
        } catch (DataConversionException | IOException e) {
            e.printStackTrace();
        }  
    }
    
    //Redo after undo of file storage change
    @Test
    public void redo_openFile_fileReOpened() {
        try {
            commandBox.runCommand(OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FILE);
            commandBox.runCommand(UndoCommand.COMMAND_WORD);
            assertRedoFileStorageSuccess(RedoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
    }
    
    //Redo storage operation when config file is invalid
    @Test
    public void redo_invalidConfig_dataConversionException() {
        commandBox.runCommand(OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FILE);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        new File(Config.DEFAULT_CONFIG_FILE).renameTo(new File(TEMP_CONFIG));
        new File(INVALID_CONFIG).renameTo(new File(Config.DEFAULT_CONFIG_FILE));
        
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertUnsuccessfulMessage(Messages.MESSAGE_CONFIG_ERROR);
        
        new File(Config.DEFAULT_CONFIG_FILE).renameTo(new File(INVALID_CONFIG));
        new File(TEMP_CONFIG).renameTo(new File(Config.DEFAULT_CONFIG_FILE));
    }
    
    private void assertRedoSuccess(String message, Task... expectedHits) {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);

        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
        assertSuccessfulMessage(message);
    }
    
    //Assertion for redo in change of directory
    private void assertRedoDirectorySuccess(String message) throws DataConversionException {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER_TEMP.substring(0, TEST_DATA_FOLDER_TEMP.length() - 1)));
        assertSuccessfulMessage(message);
    }
  
    //Assertion for redo in change of file storage
    private void assertRedoFileStorageSuccess(String message) throws DataConversionException {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFilePath().contains(TEST_DATA_FILE.substring(0, TEST_DATA_FILE.length() - 1)));
        assertSuccessfulMessage(message);
    }
    
    //Restores original taskManager directory
    public void restoreStorage() throws IOException {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
    }
    
    //Stores original taskManager directory and file name
    @Before
    public void setUp() throws DataConversionException, IOException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        taskManagerDirectory = config.getTaskManagerFileDirectory();
        taskManagerFileName = config.getTaskManagerFileName();
        
        config.setTaskManagerFileDirectory(TEST_DATA_FOLDER);
        config.setTaskManagerFileName(TEST_DATA_FILE);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
    }
    
    //Restores original taskManager directory and file name
    @After
    public void tearDown() throws IOException {
        config.setTaskManagerFileDirectory(taskManagerDirectory);
        config.setTaskManagerFileName(taskManagerFileName);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
    }
}
