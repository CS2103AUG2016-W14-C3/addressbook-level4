package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.logic.commands.AddCommand;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

public class AddCommandTest extends AddressBookGuiTest {

    @Test
    public void add() {
        //add one task
        Task[] currentList = td.getTypicalTasks();
        Task taskToAdd = td.helpFriend;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add another task
        taskToAdd = td.interview;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add duplicate task
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " 
                + td.helpFriend.getName());
        assertResultMessage(AddCommand.MESSAGE_DUPLICATE_TASK);
        assertTrue(taskListPanel.isListMatching(currentList));

        //add to empty list
        commandBox.runCommand("clear");
        currentList = new Task[0];
        taskToAdd = td.attendMeeting;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //invalid command
        commandBox.runCommand("adds Johnny");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
        
        //Invalid event add format
        commandBox.runCommand("add watch movie with friends by 7pm to 9pm");
        assertResultMessage(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, 
                AddCommand.MESSAGE_USAGE));
    }
    
    private void assertAddSuccess(Task taskToAdd, Task... currentList) {
        commandBox.runCommand(AddCommand.COMMAND_WORD + " "
                + taskToAdd.toString());

        //confirm the new card contains the right data
        TaskCardHandle addedCard = taskListPanel.navigateToTask(taskToAdd.getName().fullName);
        assertMatching(taskToAdd, addedCard);

        //confirm the list now contains all previous tasks plus the new task
        Task[] expectedList = TestUtil.addTasksToList(currentList, taskToAdd);
        assertTrue(taskListPanel.isListMatching(expectedList));
    }

}
