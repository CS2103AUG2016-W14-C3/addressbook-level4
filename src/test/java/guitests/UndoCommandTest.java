package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.DoneCommand;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.RedoCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.task.Task;

public class UndoCommandTest extends TaskManagerGuiTest {

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
        commandBox.enterCommand(EditCommand.COMMAND_WORD + " 1 " + td.helpFriend.getName().fullName);
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
    }
    
    private void assertUndoSuccess(Task... expectedHits) {
        commandBox.runCommand(UndoCommand.COMMAND_WORD);
        
        //Confirms the list size remains the same and does reverts to its original after undo
        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
    }
}
