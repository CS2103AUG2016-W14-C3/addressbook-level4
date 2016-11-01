package guitests;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.logic.commands.AddCommand;
import taskle.logic.parser.DateParser;
import taskle.model.task.FloatTask;
import taskle.model.task.Task;
import taskle.testutil.TestUtil;

//@@author A0141780J
public class AddCommandTest extends TaskManagerGuiTest {

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

        //add duplicate task successful
        taskToAdd = new FloatTask(td.helpFriend);
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add to empty list
        commandBox.runCommand("clear");
        currentList = new Task[0];
        taskToAdd = td.attendMeeting;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //unknown command
        commandBox.runCommand("adds Johnny");
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
        
        //valid deadline add command
        taskToAdd = td.assignmentDeadline;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);
        
        //valid event add command
        taskToAdd = td.charityEvent;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //Invalid event add format
        commandBox.runCommand("add watch movie with friends by 7pm to 9pm");
        assertUnsuccessfulMessage(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, 
                AddCommand.MESSAGE_USAGE));
        
    }
    
    //@@author A0139402M
    @Test
    public void add_floatTaskWithReminders_success() {
        Task[] currentList = td.getTypicalTasks();
        String remindDate = "15 Oct 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.helpFriend;
        taskToAdd.setRemindDate(date);
        assertAddSuccess(taskToAdd, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Buy Groceries for home " 
                + "remind 15 Oct 7pm");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Buy Groceries for home Reminder on: 7:00PM, 15 Oct 2016"));
    }
    
    @Test
    public void add_deadlineTaskWithReminders_success() {
        commandBox.runCommand("clear");
        Task[] currentList = new Task[0];
        String remindDate = "15 Oct 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.assignmentDeadline;
        taskToAdd.setRemindDate(date);
        assertAddSuccess(taskToAdd, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Buy Groceries for home by 4pm 26 Oct "
                + "remind 26 Oct 3pm");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Buy Groceries for home by 4:00PM, 26 Oct 2016 Reminder on: 3:00PM, 26 Oct 2016"));
    }
    
    @Test
    public void add_eventTaskWithReminders_success() {
        commandBox.runCommand("clear");
        Task[] currentList = new Task[0];
        String remindDate = "15 Oct 7pm";
        Date date = DateParser.parse(remindDate).get(0);
        Task taskToAdd = td.charityEvent;
        taskToAdd.setRemindDate(date);
        assertAddSuccess(taskToAdd, currentList);
        
        commandBox.runCommand(AddCommand.COMMAND_WORD + " Tuition from 26 Oct 9am to 11am " 
                + "remind 26 Oct 8am");
        assertSuccessfulMessage(
                String.format(AddCommand.MESSAGE_SUCCESS,  
                "Tuition from 9:00AM to 11:00AM, 26 Oct 2016 Reminder on: 8:00AM, 26 Oct 2016"));
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

}
