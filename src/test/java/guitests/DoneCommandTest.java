package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.DoneCommand;

public class DoneCommandTest extends AddressBookGuiTest {
    /**
     * Marks a current task, inside the TypicalTestTask, as done to test the
     * done function. Check if that task has been edited correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void done_existing_task() throws IllegalValueException {
        // String newTaskName = "Buy Groceries";
        // Name newName = new Name(newTaskName);
        String index = "1";
        String command = buildCommand(index);
        // String oldName = td.attendMeeting.getName().fullName;
        // System.out.println(Integer.parseInt(index));
        assertDoneResultSuccess(command, index);
    }

    /**
     * Marks an inexistent task as done
     */
    @Test
    public void done_inexistent_task() {
        String commandInvalidIntegerIndex = buildCommand("10");
        assertEditInvalidIndex(commandInvalidIntegerIndex);

        String commandInvalidStringIndex = buildCommand("ABC");
        assertEditInvalidCommandFormat(commandInvalidStringIndex);
    }

    /**
     * Invalid done command "dones"
     */
    @Test
    public void done_invalid_command() {
        String command = "dones 1";
        assertEditInvalidCommand(command);
    }

    private String buildCommand(String taskNumber) {
        String command = DoneCommand.COMMAND_WORD + " " + taskNumber;
        return command;
    }

    private void assertDoneResultSuccess(String command, String taskNumber) {
        commandBox.runCommand(command);
        assertResultMessage("Task Completed!");
    }

    private void assertEditInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    private void assertEditInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE));
    }

    private void assertEditInvalidCommand(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
}