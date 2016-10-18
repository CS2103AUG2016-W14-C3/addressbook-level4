package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.logic.commands.AddCommand;
import taskle.logic.commands.DoneCommand;
import taskle.model.task.Task;

public class UndoCommandTest extends AddressBookGuiTest {

    @Test
    public void undo() {
        Task[] currentList = td.getTypicalTasks();
        
        //Undo with an empty history
        assertUndoSuccess(currentList);
        
        //Undo after add command
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.getName());
        assertUndoSuccess(currentList);
        
        //Undo after remove command
        commandBox.runCommand("remove 1");
        assertUndoSuccess(currentList);
        
        //Undo after edit command
        commandBox.enterCommand("edit 1 " + td.helpFriend.getName().fullName);
        assertUndoSuccess(currentList);
        
        //Undo after clear command
        commandBox.runCommand("clear");
        assertUndoSuccess(currentList);
        
        //Undo after reschedule command
        commandBox.runCommand("reschedule 1 18 Oct");
        assertUndoSuccess(currentList);
        
        //Undo after done command
        commandBox.runCommand(DoneCommand.COMMAND_WORD);
        assertUndoSuccess(currentList);
    }
    
    private void assertUndoSuccess(Task... expectedHits) {
        commandBox.runCommand("undo");
        
        //Confirms the list size remains the same
        assertListSize(expectedHits.length);
        assertTrue(taskListPanel.isListMatching(expectedHits.length));
    }
}
