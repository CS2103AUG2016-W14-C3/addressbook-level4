package guitests;

import static org.junit.Assert.assertTrue;
import static taskle.logic.commands.RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS;

import org.junit.Test;

import taskle.model.task.Task;
import taskle.testutil.TestUtil;

public class RemoveCommandTest extends TaskManagerGuiTest {

    @Test
    public void remove() {

        //delete the first in the list
        Task[] currentList = td.getTypicalTasks();
        int targetIndex = 1;
        assertRemoveSuccess(targetIndex, currentList);

        //delete the last in the list
        currentList = TestUtil.removeTaskFromList(currentList, targetIndex);
        targetIndex = currentList.length;
        assertRemoveSuccess(targetIndex, currentList);
        
        //invalid index
        commandBox.runCommand("remove " + currentList.length + 1);
        assertUnsuccessfulMessage("The task index provided is invalid");

        //delete from the middle of the list
        currentList = TestUtil.removeTaskFromList(currentList, targetIndex);
        targetIndex = currentList.length/2;
        assertRemoveSuccess(targetIndex, currentList);

    }

    /**
     * Runs the delete command to delete the person at specified index and confirms the result is correct.
     * @param targetIndexOneIndexed e.g. to delete the first person in the list, 1 should be given as the target index.
     * @param currentList A copy of the current list of persons (before deletion).
     */
    private void assertRemoveSuccess(int targetIndexOneIndexed, final Task[] currentList) {
        Task taskToDelete = currentList[targetIndexOneIndexed-1]; //-1 because array uses zero indexing
        Task[] expectedRemainder = TestUtil.removeTaskFromList(currentList, targetIndexOneIndexed);

        commandBox.runCommand("remove " + targetIndexOneIndexed);

        //confirm the list now contains all previous tasks except the deleted task
        assertTrue(taskListPanel.isListMatching(expectedRemainder));

        //confirm the result message is correct
        assertSuccessfulMessage(String.format(MESSAGE_DELETE_TASK_SUCCESS, targetIndexOneIndexed));
    }

}
