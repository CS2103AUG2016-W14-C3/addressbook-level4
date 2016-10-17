package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Date;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.parser.DateParser;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;

public class RescheduleCommandTest extends AddressBookGuiTest {

    /**
     * Reschedules a current task to a float task inside the TypicalTestTask to
     * test the reschedule function. Check if that task has been edited
     * correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void reschedule_task_to_floattask() throws IllegalValueException {
        String index = "1";
        String name = td.attendMeeting.getName().fullName;
        String oldDate = td.attendMeeting.getDetailsString();
        assertRescheduleResultSuccess("reschedule " + index + " clear", name + "\t" + oldDate + " -> " + "");

        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(new Name(name), new UniqueTagList());
        assertMatching(newTask, addedCard);
    }

    /**
     * Reschedules a current task to a deadline task inside the TypicalTestTask
     * to test the reschedule function. Check if that task has been edited
     * correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void reschedule_task_to_deadlinetask() throws IllegalValueException {
        String newDate = "21 Oct 3pm";
        String index = "1";
        String command = buildCommand(index, newDate);
        String name = td.attendMeeting.getName().fullName;
        String oldDate = td.attendMeeting.getDetailsString();
        Date date = DateParser.parse(newDate).get(0);
        assertRescheduleResultSuccess(command, name + "\t" + oldDate + " -> " + "3:00PM, 21 Oct 2016");

        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        DeadlineTask newTask = new DeadlineTask(new Name(name), date, new UniqueTagList());
        assertMatching(newTask, addedCard);
    }

    /**
     * Reschedules a current task to a event task inside the TypicalTestTask to
     * test the reschedule function. Check if that task has been edited
     * correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void reschedule_task_to_eventtask() throws IllegalValueException {
        String newDate = "21 Oct 3pm to 31 Oct 5pm";
        String index = "1";
        String command = buildCommand(index, newDate);
        String name = td.attendMeeting.getName().fullName;
        String oldDate = td.attendMeeting.getDetailsString();
        Date startDate = DateParser.parse(newDate).get(0);
        Date endDate = DateParser.parse(newDate).get(1);
        assertRescheduleResultSuccess(command,
                name + "\t" + oldDate + " -> " + "3:00PM, 21 Oct 2016 to 5:00PM, 31 Oct 2016");

        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        EventTask newTask = new EventTask(new Name(name), startDate, endDate, new UniqueTagList());
        assertMatching(newTask, addedCard);
    }

    /**
     * Reschedules an inexistent task
     */
    @Test
    public void reschedule_inexistent_task() {
        String commandInvalidIntegerIndex = buildCommand("10", "31 Oct 10pm");
        assertRescheduleInvalidIndex(commandInvalidIntegerIndex);

        String commandInvalidStringIndex = buildCommand("ABC", "31 Oct 10pm");
        assertRescheduleInvalidCommandFormat(commandInvalidStringIndex);

        String commandInvalidNegativeIntegerIndex = buildCommand("-1", "31 Oct 10pm");
        assertRescheduleInvalidCommandFormat(commandInvalidNegativeIntegerIndex);
    }

    /**
     * Reschedules a task with more than 2 dates
     */
    @Test
    public void reschedule_more_than_2_dates() {
        String commandTooManyDates = buildCommand("1", "31 Oct 10pm to 1 Nov 11pm to 12 Dec 12pm");
        assertRescheduleInvalidCommandFormat(commandTooManyDates);
    }

    /**
     * Reschedules a task with no date
     */
    @Test
    public void reschedule_task_no_date() {
        String commandNoDates = buildCommand("1", "no date");
        assertRescheduleInvalidCommandFormat(commandNoDates);
    }
    private String buildCommand(String taskNumber, String date) {
        String command = RescheduleCommand.COMMAND_WORD + " " + taskNumber + " " + date;
        return command;
    }

    private void assertRescheduleResultSuccess(String command, String newName) {
        commandBox.runCommand(command);
        assertResultMessage("Rescheduled Task: " + newName);
    }

    private void assertRescheduleInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    private void assertRescheduleInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE));
    }

}
