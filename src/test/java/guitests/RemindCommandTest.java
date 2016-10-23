package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.RemindCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.parser.DateParser;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;

public class RemindCommandTest extends TaskManagerGuiTest {

    /**
     * Set a reminder for a current task inside the TypicalTestTask to
     * test the remind function. Check if that task has been edited
     * correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void remind_task_success() throws IllegalValueException {
        String index = "1";
        String name = td.attendMeeting.getName().fullName;
        String newRemindDateString = "13 Dec 7pm";
        String oldRemindDate = td.attendMeeting.getRemindDetailsString();
        assertRemindResultSuccess("remind " + index + " " + newRemindDateString, 
                name + " " + oldRemindDate + " -> " + "7:00PM, 13 Dec 2016");
        Date date = DateParser.parse(newRemindDateString).get(0);
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name), date, new UniqueTagList());
        assertMatching(newTask, addedCard);
    }

    /**
     * Set a reminder with no specified time to a task
     * @throws IllegalValueException
     */
    @Test
    public void remind_task_no_time_success() throws IllegalValueException {
        String index = "1";
        String name = td.attendMeeting.getName().fullName;
        String newRemindDateString = "13 Dec";
        String oldRemindDate = td.attendMeeting.getRemindDetailsString();
        assertRemindResultSuccess("remind " + index + " " + newRemindDateString, 
                name + " " + oldRemindDate + " -> " + "12:00AM, 13 Dec 2016");
        Date date = DateParser.parse(newRemindDateString).get(0);
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name), date, new UniqueTagList());
        assertMatching(newTask, addedCard);
    }
    
    /**
     * Set reminder to an inexistent task
     */
    @Test
    public void remind_inexistent_task() {
        String commandInvalidIntegerIndex = buildCommand("10", "31 Oct 10pm");
        assertRescheduleInvalidIndex(commandInvalidIntegerIndex);

        String commandInvalidStringIndex = buildCommand("ABC", "31 Oct 10pm");
        assertRescheduleInvalidCommandFormat(commandInvalidStringIndex);

        String commandInvalidNegativeIntegerIndex = buildCommand("-1", "31 Oct 10pm");
        assertRescheduleInvalidCommandFormat(commandInvalidNegativeIntegerIndex);
    }

    /**
     * Set a reminder with more than 1 date
     */
    @Test
    public void remind_more_than_1_dates() {
        String commandTooManyDates = buildCommand("1", "31 Oct 10pm to 1 Nov 11pm");
        assertRescheduleInvalidCommandFormat(commandTooManyDates);
    }

    /**
     * Set a reminder with an invalid date
     */
    @Test
    public void remind_task_invalid_date() {
        String commandNoDates = buildCommand("1", "no date");
        assertRescheduleInvalidCommandFormat(commandNoDates);
    }
    
    private String buildCommand(String taskNumber, String date) {
        String command = RemindCommand.COMMAND_WORD + " " + taskNumber + " " + date;
        return command;
    }

    private void assertRemindResultSuccess(String command, String newName) {
        commandBox.runCommand(command);
        assertResultMessage("Set Reminder Date: " + newName);
    }

    private void assertRescheduleInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    private void assertRescheduleInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE));
    }

}
