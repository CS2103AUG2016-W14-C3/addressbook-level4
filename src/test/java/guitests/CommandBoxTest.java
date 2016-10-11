package guitests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import taskle.logic.commands.AddCommand;

public class CommandBoxTest extends AddressBookGuiTest {

    @Test
    public void commandBox_commandSucceeds_textCleared() {
        commandBox.runCommand(AddCommand.COMMAND_WORD + " " + td.buyMilk.toString());
        assertEquals(commandBox.getCommandInput(), "");
    }

    @Test
    public void commandBox_commandFails_textStays(){
        commandBox.runCommand("invalid command");
        assertEquals(commandBox.getCommandInput(), "invalid command");
        //TODO: confirm the text box color turns to red
    }

}
