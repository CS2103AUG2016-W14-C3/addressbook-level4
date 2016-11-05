package guitests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
import taskle.commons.util.StorageUtil;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.DoneCommand;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.OpenFileCommand;
import taskle.logic.commands.RedoCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.task.Task;

//@@author A0140047U
public class UndoCommandTest extends TaskManagerGuiTest {

    private static String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest");
    private static String TEST_DATA_TEMP_FOLDER = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest/temp");
    private static String TEST_DATA_FILE_NAME = "ValidFormatTaskManager.xml";
    private static String TEST_DATA_ANOTHER_FILE_NAME = "AnotherValidFormatTaskManager.xml";
    private static String TEST_DATA_FILE_PATH = TEST_DATA_TEMP_FOLDER + File.separator + TEST_DATA_ANOTHER_FILE_NAME;
    
    private Config config;
    private String taskManagerPath;
    private String taskManagerDirectory;
    private String taskManagerFileName;
    
    //Undo with an empty history
    @Test
    public void undo_emptyHistory_messageDisplayed() {
        StorageUtil.clearHistory();
        Task[] currentList = td.getTypicalTasks();
        assertUndoSuccess(UndoCommand.MESSAGE_EMPTY_HISTORY, currentList);
    }
    
    //Undo after add command
    @Test
    public void undo_addCommand_taskListRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Undo after edit command
    @Test
    public void undo_editCommand_taskListRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(EditCommand.COMMAND_WORD + " 1 " + td.helpFriend.getName());
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Undo after remove command
    @Test
    public void undo_removeCommand_taskListRestored() {  
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(RemoveCommand.COMMAND_WORD + " 1");
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Undo after clear command
    @Test
    public void undo_clearCommand_taskListRemoved() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(ClearCommand.COMMAND_WORD);
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);    
    }
    
    //Undo after reschedule command 
    @Test
    public void undo_rescheduleCommand_taskListRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(RescheduleCommand.COMMAND_WORD + " 1 18 Oct");
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Undo after done command        
    @Test
    public void undo_doneCommand_taskListRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(DoneCommand.COMMAND_WORD + " 1");
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
        
    }
    
    //Undo after redo command        
    @Test 
    public void undo_redoCommand_taskListRestored() {
        Task[] currentList = td.getTypicalTasks();
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertUndoSuccess(UndoCommand.MESSAGE_SUCCESS, currentList);
    }
    
    //Undo after storage directory change
    @Test
    public void undo_changeDirectory_taskListRestored() {
        try {
            commandBox.runCommand(ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_TEMP_FOLDER);
            assertUndoStorageSuccess(UndoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
    }
    
    //Undo after file storage change
    @Test
    public void undo_openFile_taskListRestored() {
        try {
            commandBox.runCommand(OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FILE_PATH);
            assertUndoStorageSuccess(UndoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
    }
    
    private void assertUndoSuccess(String message, Task... expectedHits) {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        //Confirms the list size remains the same and does reverts to its original after undo
        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
        assertSuccessfulMessage(message);
    }
    
    //Assertion for undo in change of directory
    private void assertUndoStorageSuccess(String message) throws DataConversionException {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFilePath().equals(taskManagerPath));
        assertSuccessfulMessage(message);
    }
  
    //Stores original taskManager directory and file name
    @Before
    public void setUp() throws DataConversionException, IOException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        taskManagerDirectory = config.getTaskManagerFileDirectory();
        taskManagerFileName = config.getTaskManagerFileName();
        
        File tempDirectory = new File(TEST_DATA_FOLDER);
        config.setTaskManagerFileDirectory(tempDirectory.getAbsolutePath());
        config.setTaskManagerFileName(TEST_DATA_FILE_NAME);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
        
        taskManagerPath = config.getTaskManagerFilePath();
    }
    
    //Restores original taskManager directory and file name
    @After
    public void tearDown() throws IOException {
        config.setTaskManagerFileDirectory(taskManagerDirectory);
        config.setTaskManagerFileName(taskManagerFileName);
        ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
    }
}
