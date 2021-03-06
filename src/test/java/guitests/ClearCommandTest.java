package guitests;

import org.junit.Test;

import taskle.logic.commands.AddCommand;

import static org.junit.Assert.assertTrue;

public class ClearCommandTest extends TaskManagerGuiTest {

    @Test
    public void clear() {

        //verify a non-empty list can be cleared
        assertTrue(taskListPanel.isListMatching(td.getTypicalTasks()));
        assertClearCommandSuccess();

        //verify other commands can work after a clear command
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.helpFriend.toString());
        assertTrue(taskListPanel.isListMatching(td.helpFriend));
        commandBox.runCommand("remove 1");
        assertListSize(0);

        //verify clear command works when the list is empty
        assertClearCommandSuccess();
    }

    private void assertClearCommandSuccess() {
        commandBox.runCommand("clear");
        assertListSize(0);
        assertSuccessfulMessage("Taskle has been Cleared!");
    }
}
