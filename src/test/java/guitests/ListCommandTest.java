package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taskle.commons.core.Messages;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

/**
 * Unit test class that tests for List command executions in GUI.
 * @author Abel
 *
 */
public class ListCommandTest extends TaskManagerGuiTest {

    //@@author A0141780J
    @Test
    public void listCommand_listNonEmptyList_returnsCorrectResults() {
        // list after removing one task
        commandBox.runCommand("remove 1");
        Task[] currentList = td.getTypicalTasks();
        currentList = TestUtil.removeTaskFromList(currentList, 1);
        assertListResult("list", true, false, true, currentList);
    }

    @Test
    public void listCommand_listDoneTasks_returnsDoneTasks(){
        commandBox.runCommand("done 1");
        commandBox.runCommand("done 1");
        assertListResult("list -done", 
                false, true, false,
                new Task[] { td.charityEvent, td.assignmentDeadline }); 
    }
    
    @Test
    public void listCommand_listPendingOverdueTask_returnsPendingOverdueTasks(){
        commandBox.runCommand("done 2");
        Task[] currentList = td.getTypicalTasks();
        currentList = TestUtil.removeTaskFromList(currentList, 2);
        assertListResult("list -overdue -pending", 
                true, false, true, 
                currentList); 
    }

    @Test
    public void listCommand_findInvalidCommand_fail() {
        commandBox.runCommand("findgeorge");
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
    
    
    @Test
    public void listCommand_listAll_returnsAllTasks(){
        assertListResult("list -all", true, true, true, td.getTypicalTasks());
    }

    private void assertListResult(String command, boolean showPending, 
            boolean showDone, boolean showOverdue, Task... expectedHits ) {
        commandBox.runCommand(command);
        assertListSize(expectedHits.length);
        String[] msgStrings = new String[] {
            "Not Pending", "Not Done", "Not Overdue"
        };
        if (showPending) {
            msgStrings[0] = "Pending";
        }
        
        if (showDone) {
            msgStrings[1] = "Done";
        }
        
        if (showOverdue) {
            msgStrings[2] = "Overdue";
        }
        
        assertSuccessfulMessage(
                "Listed " + String.join(", ", msgStrings) + " tasks");
        assertTrue(taskListPanel.isListMatching(expectedHits));
        assertShownStatuses(showPending, showDone, showOverdue);
    }
}
