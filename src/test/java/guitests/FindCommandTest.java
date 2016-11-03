package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.commons.core.Messages;
import taskle.model.task.Task;

//@@author A0141780J
public class FindCommandTest extends TaskManagerGuiTest {

    @Test
    public void findCommand_findNonEmptyList_returnsCorrectResults() {
        assertFindResult("find Mark"); //no results
        assertFindResult("find Milk", td.buyMilk, td.deliverGoods); //multiple results

        //find after removing one task
        
        commandBox.runCommand("remove 1");
        assertFindResult("find Milk", td.deliverGoods);
    }

    @Test
    public void findCommand_findDoneTask_returnsDoneTasks(){
        commandBox.runCommand("done 1");
        assertFindResult("find Charity -done", td.charityEvent); 
    }
    
    @Test
    public void findCommand_findPendingDoneTask_returnsPendingDoneTasks(){
        commandBox.runCommand("done 1");
        assertFindResult("find Charity -done -pending", td.charityEvent); 
    }

    @Test
    public void findCommand_findInvalidCommand_fail() {
        commandBox.runCommand("findgeorge");
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
    
    @Test
    public void findCommand_findEmptyList_returnsNoResults(){
        commandBox.runCommand("clear");
        assertFindResult("find Milk"); //no results
    }

    private void assertFindResult(String command, Task... expectedHits ) {
        commandBox.runCommand(command);
        assertListSize(expectedHits.length);
        assertSuccessfulMessage(expectedHits.length + " task listed!");
        assertTrue(taskListPanel.isListMatching(expectedHits));
    }
}
