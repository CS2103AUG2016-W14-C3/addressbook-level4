package taskle.logic.parser;

import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.commands.Command;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.IncorrectCommand;

//@@author A0139402M
/**
 * EditCommandParser class to handle parsing of commands
 *
 */
//@@author A0139402M
public class EditCommandParser extends CommandParser {

    public EditCommandParser() {
    }

    @Override
    public boolean canParse(String commandWord) {
        assert (commandWord != null && !commandWord.isEmpty());
        return commandWord.equals(EditCommand.COMMAND_WORD)
               || commandWord.equals(EditCommand.COMMAND_WORD_SHORT);
    }

    @Override
    public Command parseCommand(String args) {
        return prepareEdit(args);
    }

    /**
     * Parses arguments in the context of the edit task command
     * 
     * @param args
     * @return the prepared command with the task number and the new task name
     */
    private Command prepareEdit(String args) {
        args = args.trim();
        int endIndex = args.indexOf(" ");
        if (endIndex == -1) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  EditCommand.MESSAGE_USAGE));
        }
        
        String indexValue = args.substring(0, endIndex);
        String newName = args.substring(endIndex).trim();
        Optional<Integer> index = parseIndex(indexValue);
        Optional<String> name = parseName(newName);
        if (!index.isPresent() || !name.isPresent()) {
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                                  EditCommand.MESSAGE_USAGE));
        }
        
        try {
            return new EditCommand(index.get(), name.get());
        } catch (IllegalValueException e) {
            return new IncorrectCommand(e.getMessage());
        }
    }
}
