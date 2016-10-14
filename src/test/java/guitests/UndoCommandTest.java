package guitests;

import org.junit.Test;

import taskle.testutil.TestTask;

public class UndoCommandTest extends AddressBookGuiTest {

    @Test
    public void undo() {
        TestTask[] currentList = td.getTypicalTasks();
        
        //Undo with an empty history
        assertUndoSuccess(currentList);
        
        //Undo after add command
        commandBox.runCommand(td.helpFriend.getAddCommand());
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
    }
    
    private void assertUndoSuccess(TestTask... expectedHits) {
        commandBox.runCommand("undo");
        
        //Confirms the list size remains the same
        assertListSize(expectedHits.length);
    }
}
