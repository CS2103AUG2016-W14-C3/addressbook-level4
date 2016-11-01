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
