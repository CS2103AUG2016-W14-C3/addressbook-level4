package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.Test;

import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.DoneCommand;

//@@author A0125509H

public class DoneCommandTest extends TaskManagerGuiTest {
    
    /**
     * Marks a current task, inside the TypicalTestTask, as done to test the
     * done function. Check if that task has been edited correctly.
     * 
     * @throws IllegalValueException
     */
    @Test
    public void done_existing_task() throws IllegalValueException {
        String index = "1";
        String command = buildCommand(index);
        assertDoneResultSuccess(command, index);
    }

    /**
     * Marks an inexistent task as done
     */
    @Test
    public void done_inexistent_task() {
        String commandInvalidIntegerIndex = buildCommand("10");
        assertDoneInvalidIndex(commandInvalidIntegerIndex);

        String commandInvalidStringIndex = buildCommand("ABC");
        assertDoneInvalidCommandFormat(commandInvalidStringIndex);
    }

    /**
     * Invalid done command "dones"
     */
    @Test
    public void done_invalid_command() {
        String command = "dones 1";
        assertDoneInvalidCommand(command);
    }

    private String buildCommand(String taskNumber) {
        String command = DoneCommand.COMMAND_WORD + " " + taskNumber;
        return command;
    }

    private void assertDoneResultSuccess(String command, String taskNumber) {
        commandBox.runCommand(command);
        assertSuccessfulMessage("Task Completed!");
    }

    private void assertDoneInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    private void assertDoneInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE));
    }

    private void assertDoneInvalidCommand(String command) {
        commandBox.runCommand(command);
        assertUnsuccessfulMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
}
