package guitests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
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
    
    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/StorageDirectoryUtilTest/");
    private static String TEST_DATA_FILE = TEST_DATA_FOLDER + "ValidFormatTaskManager.xml";
    
    private Config config;
    private String taskManagerDirectory;
    private String taskManagerFileName;
    
    @Test
    public void redo() {
        Task[] currentList = td.getTypicalTasks();
        
        //Redo when no action has been undone
        assertRedoSuccess(RedoCommand.MESSAGE_NOTHING_TO_REDO, currentList);
        
        //Redo after undo of mutating command
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        currentList = TestUtil.addTasksToList(currentList, td.helpFriend);
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        assertRedoSuccess(RedoCommand.MESSAGE_SUCCESS, currentList);
        
        //Redo after mutating command, should show "Nothing to Redo" message
        commandBox.runCommand(RemoveCommand.COMMAND_WORD + " 1");
        currentList = TestUtil.removeTaskFromList(currentList, 1);
        assertRedoSuccess(RedoCommand.MESSAGE_NOTHING_TO_REDO, currentList);
        
        //Redo after undo of storage directory change
        try {
            commandBox.runCommand(ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER);
            commandBox.runCommand(UndoCommand.COMMAND_WORD);
            assertRedoDirectorySuccess(RedoCommand.MESSAGE_SUCCESS);
            restoreStorage();
        } catch (DataConversionException | IOException e) {
            e.printStackTrace();
        }
        
        //Redo after undo of file storage change
        try {
            commandBox.runCommand(OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FILE);
            commandBox.runCommand(UndoCommand.COMMAND_WORD);
            assertRedoFileStorageSuccess(RedoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
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
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
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
    public void setUp() throws DataConversionException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        taskManagerDirectory = config.getTaskManagerFileDirectory();
        taskManagerFileName = config.getTaskManagerFileName();
    }
    
    //Restores original taskManager directory and file name
    @After
    public void tearDown() throws IOException {
        config.setTaskManagerFileDirectory(taskManagerDirectory);
        config.setTaskManagerFileName(taskManagerFileName);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
    }
}
