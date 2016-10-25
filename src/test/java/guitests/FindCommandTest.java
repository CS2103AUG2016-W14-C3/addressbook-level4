package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.commons.core.Messages;
import taskle.model.task.Task;

//@@author A0141780J
public class FindCommandTest extends TaskManagerGuiTest {

    @Test
    public void execute_findNonEmptyList_returnsCorrectResults() {
        assertFindResult("find Mark"); //no results
        assertFindResult("find Milk", td.buyMilk, td.deliverGoods); //multiple results

        //find after deleting one result
        
        commandBox.runCommand("remove 1");
        assertFindResult("find Milk", td.deliverGoods);
    }

    @Test
    public void execute_findEmptyList_returnsNoResults(){
        commandBox.runCommand("clear");
        assertFindResult("find Jean"); //no results
    }

    @Test
    public void execute_findInvalidCommand_fail() {
        commandBox.runCommand("findgeorge");
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    private void assertFindResult(String command, Task... expectedHits ) {
        commandBox.runCommand(command);
        assertListSize(expectedHits.length);
        assertSuccessfulMessage(expectedHits.length + " task listed!");
        assertTrue(taskListPanel.isListMatching(expectedHits));
    }
}
