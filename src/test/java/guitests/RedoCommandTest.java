package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.logic.commands.AddCommand;
import taskle.logic.commands.RedoCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

public class RedoCommandTest extends TaskManagerGuiTest {
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
    }
    
    private void assertRedoSuccess(String message, Task... expectedHits) {
        commandBox.runCommand(RedoCommand.COMMAND_WORD);

        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
        assertResultMessage(message);
    }
}
