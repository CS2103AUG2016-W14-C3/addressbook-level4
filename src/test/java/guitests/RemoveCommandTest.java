package guitests;

import static org.junit.Assert.assertTrue;
import static taskle.logic.commands.RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import taskle.model.task.Task;
import taskle.testutil.TestUtil;

public class RemoveCommandTest extends TaskManagerGuiTest {

    //@@author A0125509H
    @Test
    public void removeCommand_removeFirstTask_successfulRemove() {
        // removes the first in the list
        Task[] currentList = td.getTypicalTasks();
        int targetIndex = 1;
        assertRemoveSuccess(targetIndex, currentList);
    }
    
    @Test
    public void removeCommand_removeLastTask_successfulRemove() {
        // removes the last in the list
        Task[] currentList = td.getTypicalTasks();
        int targetIndex = currentList.length;
        assertRemoveSuccess(targetIndex, currentList);
    }
    
    @Test
    public void removeCommand_invalidIndex_showUnsuccessfulMsg() {
        //  invalid index
        Task[] currentList = td.getTypicalTasks();
        int targetIndex = currentList.length + 1;
        commandBox.runCommand("remove " + targetIndex);
        assertUnsuccessfulMessage("The task index provided is invalid");
    }
    
    @Test
    public void removeCommand_multipleRemove_successfulRemove() {
        //remove multiple
        Task[] currentList = td.getTypicalTasks();
        String targetIndexString = "2 4 1";
        assertRemoveSuccessString(targetIndexString, currentList);
    }
    
    @Test
    public void removeCommand_removeFromMiddle_successfulRemove() {
        // middle index
        Task[] currentList = td.getTypicalTasks();
        int targetIndex = currentList.length/2;
        assertRemoveSuccess(targetIndex, currentList);
    }
    
    @Test
    public void removeCommand_multipleRemoveWithInvalidIndex_showUnsuccessfulMsg() {
        //remove multiple
        Task[] currentList = td.getTypicalTasks();
        String targetIndexString = "2 4 1 index";
        //assertRemoveSuccessString(targetIndexString, currentList);
        commandBox.runCommand("remove " + targetIndexString);
        assertUnsuccessfulMessage("Invalid command format!");
    }

    /**
     * Runs the delete command to delete the person at specified index and confirms the result is correct.
     * @param targetIndexOneIndexed e.g. to delete the first person in the list, 1 should be given as the target index.
     * @param currentList A copy of the current list of per
     * sons (before deletion).
     */
    private void assertRemoveSuccess(int targetIndexOneIndexed, final Task[] currentList) {
        Task[] expectedRemainder = TestUtil.removeTaskFromList(currentList, targetIndexOneIndexed);

        commandBox.runCommand("remove " + targetIndexOneIndexed);

        //confirm the list now contains all previous tasks except the deleted task
        assertTrue(taskListPanel.isListMatching(expectedRemainder));

        //confirm the result message is correct
        assertSuccessfulMessage(String.format(MESSAGE_DELETE_TASK_SUCCESS, 
                                              targetIndexOneIndexed));
    }
    
    private void assertRemoveSuccessString(String targetIndexOneIndexedString, final Task[] currentList) {
        ArrayList<Integer> targetIndexOneIndexed = new ArrayList<Integer>();
        Task[] expectedRemainder = Arrays.copyOf(
                currentList, currentList.length);
        
        String argsTrim = targetIndexOneIndexedString.trim();
        String[] s = argsTrim.split(" ");
        String compareString = "";
        for (int i = 0; i < s.length; i++) {
            targetIndexOneIndexed.add(Integer.parseInt(s[i]));
            
            if(i != (s.length - 1)) { 
                compareString = compareString 
                        + targetIndexOneIndexed.get(i) + ", ";
            } else { 
                compareString = compareString + targetIndexOneIndexed.get(i);
            }
        }
        Collections.sort(targetIndexOneIndexed);
        Collections.reverse(targetIndexOneIndexed);
        
        commandBox.runCommand("remove " + targetIndexOneIndexedString);
        
        for(int j = 0; j < s.length; j++) {
            expectedRemainder = TestUtil.removeTaskFromList(expectedRemainder, targetIndexOneIndexed.get(j));
        }
        
        assertTrue(taskListPanel.isListMatching(expectedRemainder));
        assertSuccessfulMessage(String.format(MESSAGE_DELETE_TASK_SUCCESS, 
                                              compareString));
    }
}
