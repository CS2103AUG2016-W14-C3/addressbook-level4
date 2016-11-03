package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.commons.util.DateFormatUtil;
import taskle.logic.commands.RemindCommand;
import taskle.logic.parser.DateParser;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;

//@@author A0139402M
public class RemindCommandTest extends TaskManagerGuiTest {

    /**
     * Set a reminder for a current task inside the TypicalTestTask to
     * test the remind function. Check if that task has been edited
     * correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void setReminder_normalDateSet_success() throws IllegalValueException {
        String index = "3";
        TaskCardHandle oldTask = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        String name = oldTask.getFullName();
        String newRemindDateString = "13 Dec 7pm";
        Date date = DateParser.parse(newRemindDateString).get(0);
        String formattedNewDate = DateFormatUtil.formatDate(date);
        String oldRemindDate = oldTask.getRemindDetails();
        assertRemindResultSuccess("remind " + index + " " + newRemindDateString, 
                name + " " + oldRemindDate + " -> " + formattedNewDate);
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name), date);
        assertMatching(newTask, addedCard);
    }

    /**
     * Set a reminder with no specified time to a task
     * @throws IllegalValueException
     */
    @Test
    public void setReminder_taskNoInputTime_success() throws IllegalValueException {
        String index = "3";
        TaskCardHandle oldTask = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        String name = oldTask.getFullName();
        String newRemindDateString = "13 Dec";
        Date date = DateParser.parse(newRemindDateString).get(0);
        String formattedNewDate = DateFormatUtil.formatRemindDate(date);
        String oldRemindDate = oldTask.getRemindDetails();
        assertRemindResultSuccess("remind " + index + " " + newRemindDateString, 
                name + " " + oldRemindDate + " -> " + formattedNewDate);
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name), date);
        assertMatching(newTask, addedCard);
    }
    
    /**
     * Set a reminder with no specified time to a task
     * @throws IllegalValueException
     */
    @Test
    public void setReminder_taskClearReminder_success() throws IllegalValueException {
        String index = "10";
        TaskCardHandle oldTask = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        
        String name = oldTask.getFullName();
        String clearRemindDate = "clear";
        String oldRemindDate = oldTask.getRemindDetails();
        assertRemindResultSuccess("remind " + index + " " + clearRemindDate, 
                name + " " + oldRemindDate + " -> " + "");
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name));
        assertMatching(newTask, addedCard);
    }
    
    /**
     * Failure when reminder set is of a date that is later than the end date
     * of the event task
     * @throws IllegalValueException
     */
    @Test
    public void setReminder_reminderPastEventTaskEndDate_failure() throws IllegalValueException {
        String index = "1";
        String newRemindDateString = "13 Dec 7pm";
        assertRemindPastEndDate("remind " + index + " " + newRemindDateString);
    }

    /**
     * Failure when reminder set is of a date that is later than the end date
     * of the deadline task
     * @throws IllegalValueException
     */
    @Test
    public void setReminder_reminderPastDeadlineTaskEndDate_failure() throws IllegalValueException {
        String index = "2";
        String newRemindDateString = "13 Dec 7pm";
        assertRemindPastEndDate("remind " + index + " " + newRemindDateString);
    }
    
   
    
    /**
     * Set reminder to an inexistent task
     */
    @Test
    public void setReminder_inexistentTask_failure() {
        String commandInvalidIntegerIndex = buildCommand("99", "31 Oct 10pm");
        assertRemindInvalidIndex(commandInvalidIntegerIndex);

        String commandInvalidStringIndex = buildCommand("ABC", "31 Oct 10pm");
        assertRemindInvalidCommandFormat(commandInvalidStringIndex);

        String commandInvalidNegativeIntegerIndex = buildCommand("-1", "31 Oct 10pm");
        assertRemindInvalidCommandFormat(commandInvalidNegativeIntegerIndex);
    }

    /**
     * Set a reminder with more than 1 date
     */
    @Test
    public void setReminder_moreThan1Date_failure() {
        String commandTooManyDates = buildCommand("1", "31 Oct 10pm to 1 Nov 11pm");
        assertRemindInvalidCommandFormat(commandTooManyDates);
    }

    /**
     * Set a reminder with an invalid date
     */
    @Test
    public void setReminder_taskInvalidDate_failure() {
        String commandNoDates = buildCommand("1", "no date");
        assertRemindInvalidCommandFormat(commandNoDates);
    }
    
    private String buildCommand(String taskNumber, String date) {
        String command = RemindCommand.COMMAND_WORD + " " + taskNumber + " " + date;
        return command;
    }

    private void assertRemindResultSuccess(String command, String newName) {
        commandBox.runCommand(command);
        assertSuccessfulMessage("Set Reminder Date: " + newName);
    }

    private void assertRemindInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    private void assertRemindInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE));
    }
    
    private void assertRemindPastEndDate(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE));
    }

}
