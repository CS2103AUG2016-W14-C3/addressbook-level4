package guitests;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.RemindCommand;
import taskle.logic.parser.DateParser;
import taskle.model.task.FloatTask;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

/**
 * GUI test class for AddCommand.
 * Tests that the commands issued to command box is successfully
 * processed and changes are reflected in the UI.
 * @author Abel
 *
 */
public class AddCommandTest extends TaskManagerGuiTest {
    
    //@@author A0141780J
    @Test
    public void addCommand_twoConsecutiveFloatTasks_successfulTaskAdd() {
        // add one task
        Task[] currentList = td.getTypicalTasks();
        Task taskToAdd = td.helpFriend;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);
        
        // add another different task
        taskToAdd = td.interview;
        assertAddSuccess(taskToAdd, currentList);
    }
    
    @Test
    public void addCommand_duplicateTask_successfulTaskAdd() {
        // add one task
        Task[] currentList = td.getTypicalTasks();
        
        // add duplicate task successful
        Task taskToAdd = new FloatTask(td.attendMeeting);
        assertAddSuccess(taskToAdd, currentList);
    }
    
    @Test
    public void addCommand_addToEmptyList_successfulTaskAdd() {
        // Clear task list first
        commandBox.runCommand("clear");
        
        // add one task to the empty list
        Task[] currentList = new Task[0];
        Task taskToAdd = td.attendMeeting;
        assertAddSuccess(taskToAdd, currentList);
    }
    
    @Test
    public void addCommand_invalidCommand_unknownCommandMsgShown() {
        //unknown command
        commandBox.runCommand("adds Johnny");
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
    
    @Test
    public void addCommand_addDeadline_successfulDeadlineAdd() {
        Task[] currentList = td.getTypicalTasks();
        //valid deadline add command
        Task taskToAdd = td.assignmentDeadline;
        assertAddSuccess(taskToAdd, currentList);
    }
    
    @Test
    public void addCommand_addEvent_successfulEventAdd() {
        Task[] currentList = td.getTypicalTasks();
        //valid deadline add command
        Task taskToAdd = td.charityEvent;
        assertAddSuccess(taskToAdd, currentList);
    }
    
    @Test
    public void addCommand_addInvalidEvent_invalidCommandMsgShown() {
        //Invalid event add format
        commandBox.runCommand("add watch movie with friends by 7pm to 9pm");
        assertUnsuccessfulMessage(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, 
                AddCommand.MESSAGE_USAGE));
        
    }
    
    //@@author A0139402M
    @Test
    public void addFloatTask_withReminders_success() {
        Task[] currentList = td.getTypicalTasks();
        String remindDate = "15 Oct 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.helpFriend;
        taskToAdd.setRemindDate(date);
        assertAddWithRemindersSuccess(taskToAdd, remindDate, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Buy Groceries for home " 
                + "remind 15 Oct 7pm");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Buy Groceries for home Reminder on: 15 Oct 2016, 7:00PM"));
    }
    
    @Test
    public void addDeadlineTask_withReminders_success() {
        commandBox.runCommand("clear");
        Task[] currentList = new Task[0];
        String remindDate = "1 Oct 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.assignmentDeadline;
        taskToAdd.setRemindDate(date);
        assertAddWithRemindersSuccess(taskToAdd, remindDate, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Buy Groceries for home by 4pm 26 Oct "
                + "remind 26 Oct 3pm");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Buy Groceries for home by 26 Oct 2016, 4:00PM Reminder on: 26 Oct 2016, 3:00PM"));
    }
    
    @Test
    public void addEventTask_withReminders_success() {
        commandBox.runCommand("clear");
        Task[] currentList = new Task[0];
        String remindDate = "1 Sep 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.charityEvent;
        taskToAdd.setRemindDate(date);
        assertAddWithRemindersSuccess(taskToAdd, remindDate, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Tuition from 26 Oct 9am to 11am " 
                + "remind 26 Oct 8am");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Tuition from 26 Oct 2016, 9:00AM to 11:00AM Reminder on: 26 Oct 2016, 8:00AM"));
    }
    //@@author
    
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

    private void assertAddWithRemindersSuccess(Task taskToAdd, String reminderDate, Task... currentList) {
        commandBox.runCommand(AddCommand.COMMAND_WORD + " "
                + taskToAdd.toString() + " "
                + RemindCommand.COMMAND_WORD + " "
                + reminderDate);
        //confirm the new card contains the right data
        TaskCardHandle addedCard = taskListPanel.navigateToTask(taskToAdd.getName().fullName);
        assertMatching(taskToAdd, addedCard);

        //confirm the list now contains all previous tasks plus the new task
        Task[] expectedList = TestUtil.addTasksToList(currentList, taskToAdd);
        assertTrue(taskListPanel.isListMatching(expectedList));
    }

}
