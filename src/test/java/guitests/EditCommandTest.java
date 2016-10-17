package guitests;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import taskle.commons.core.Messages;
import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;

public class EditCommandTest extends AddressBookGuiTest{


    /**
     * Edits a current task inside the TypicalTestTask to test the edit function.
     * Check if that task has been edited correctly.
     * @throws IllegalValueException 
     */
    @Test
    public void edit_existing_task() throws IllegalValueException {
        String newTaskName = "Buy Groceries";
        String index = "1";
        Name newName = new Name(newTaskName);
        String command = buildCommand(index, newTaskName);
        String oldName = td.attendMeeting.getName().fullName;
        assertEditResultSuccess(command, oldName + " -> " + newTaskName);
        
        TaskCardHandle addedCard = taskListPanel.getTaskCardHandle(Integer.parseInt(index) - 1);
        FloatTask newTask = new FloatTask(newName, new UniqueTagList());
        assertMatching(newTask, addedCard);
    }
    
    /**
     * Edits an inexistent task
     */
    @Test
    public void edit_inexistent_task() {
        String commandInvalidIntegerIndex = buildCommand("10", "Buy dinner home");
        assertEditInvalidIndex(commandInvalidIntegerIndex);
        
        String commandInvalidStringIndex = buildCommand("ABC", "Buy dinner home");
        assertEditInvalidCommandFormat(commandInvalidStringIndex);

    }
    
    /**
     * Edits a valid task without giving a new task name
     */
    @Test
    public void edit_no_task_name() {
        String command = EditCommand.COMMAND_WORD + " 1";
        assertEditInvalidCommandFormat(command);
    }
    /**
     * Invalid edit command "edits"
     */
    @Test
    public void edit_invalid_command() {
        String command = "edits 1 Walk dog";
        assertEditInvalidCommand(command);
    }
    /**
     * Edits a task such that the new name is a duplicate of another task
     */
    @Test
    public void edit_duplicate_task() {
        String command = buildCommand("1", "Go Concert");
        assertEditDuplicateName(command);
    }
    
    private String buildCommand(String taskNumber, String newName) {
        String command = EditCommand.COMMAND_WORD + " " + taskNumber + " " + newName;
        return command;
    }
    
    private void assertEditResultSuccess(String command, String newName) {
        commandBox.runCommand(command);
        assertResultMessage("Edited Task: " + newName);
    }
    
    private void assertEditInvalidIndex(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }
    
    private void assertEditDuplicateName(String command) {
        commandBox.runCommand(command);
        assertResultMessage(EditCommand.MESSAGE_DUPLICATE_TASK);
    }
    
    private void assertEditInvalidCommandFormat(String command) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
    
    private void assertEditInvalidCommand(String command) {
        commandBox.runCommand(command);
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }
}
