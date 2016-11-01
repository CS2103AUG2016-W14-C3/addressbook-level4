package guitests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.FileUtil;
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

    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/StorageDirectoryUtilTest/");
    private static String TEST_DATA_FILE = TEST_DATA_FOLDER + "ValidFormatTaskManager.xml";
    
    private Config config;
    private String taskManagerPath;
    
    @Test
    public void undo() {
        Task[] currentList = td.getTypicalTasks();
        
        //Undo with an empty history
        assertUndoSuccess(currentList);
        
        //Undo after add command
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        assertUndoSuccess(currentList);
        
        //Undo after remove command
        commandBox.runCommand(RemoveCommand.COMMAND_WORD + " 1");
        assertUndoSuccess(currentList);
        
        //Undo after edit command
        commandBox.runCommand(EditCommand.COMMAND_WORD + " 1 " + td.helpFriend.getName());
        assertUndoSuccess(currentList);
        
        //Undo after clear command
        commandBox.runCommand(ClearCommand.COMMAND_WORD);
        assertUndoSuccess(currentList);
        
        //Undo after reschedule command
        commandBox.runCommand(RescheduleCommand.COMMAND_WORD + " 1 18 Oct");
        assertUndoSuccess(currentList);
        
        //Undo after done command
        commandBox.runCommand(DoneCommand.COMMAND_WORD + " 1");
        assertUndoSuccess(currentList);
        
        //Undo after redo command
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        commandBox.runCommand(RedoCommand.COMMAND_WORD);
        assertUndoSuccess(currentList);
        
        //Undo after storage directory change
        try {
            commandBox.runCommand(ChangeDirectoryCommand.COMMAND_WORD + " " + TEST_DATA_FOLDER);
            assertUndoStorageSuccess(UndoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        //Undo after file storage change
        try {
            commandBox.runCommand(OpenFileCommand.COMMAND_WORD + " " + TEST_DATA_FILE);
            assertUndoStorageSuccess(UndoCommand.MESSAGE_SUCCESS);
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
    }
    
    private void assertUndoSuccess(Task... expectedHits) {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        //Confirms the list size remains the same and does reverts to its original after undo
        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
    }
    
    //Assertion for undo in change of directory
    private void assertUndoStorageSuccess(String message) throws DataConversionException {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        assertTrue(config.getTaskManagerFilePath().equals(taskManagerPath));
        assertSuccessfulMessage(message);
    }
  
    @Before
    public void setUp() throws DataConversionException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        taskManagerPath = config.getTaskManagerFilePath();
    }
}
