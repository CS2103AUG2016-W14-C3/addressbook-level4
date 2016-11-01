package taskle.logic.commands;

import java.util.Date;

import taskle.commons.exceptions.IllegalValueException;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;

/**
 * Adds a task to the Task Manager.
 */
public class AddCommand extends Command {
    //@@author A0141780J

    /** stub unique tag list used for every add commands for now */
    UniqueTagList stubTagList = new UniqueTagList();

    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_WORD_SHORT = "a";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task into Taskle.\n"
            + "\nFormat: add task_name by [date + time] [remind date + time]\n"
            + "or\nadd task_name from [date + time] to [date + time] [remind date + time]\n"
            + "\nExample: " + "add Business Trip from 4 Oct to 5 Oct remind 3 Oct 2pm";

    public static final String MESSAGE_SUCCESS = "Added new task: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in Taskle!";

    private final Task toAdd;

    /**
     * Convenience constructor using raw name value.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name)
            throws IllegalValueException {
        assert (name != null);
        this.toAdd = new FloatTask(new Name(name), stubTagList);
    }
    
    /**
     * Convenience constructor using raw name value.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String nameString, Date startDate, Date endDate, Date remindDate)
                      throws IllegalValueException {
        assert (nameString != null);
        
        Name name = new Name(nameString);
        if (startDate != null && endDate != null) {
            toAdd = new EventTask(name, startDate, endDate, stubTagList);
        } else if (endDate != null) {
            toAdd = new DeadlineTask(name, endDate, stubTagList);
        } else {
            toAdd = new FloatTask(name, stubTagList);
        }
        
        if (remindDate != null) {
            toAdd.setRemindDate(remindDate);
        }
    }
    
    @Override
    public CommandResult execute() {
        assert model != null;        
        model.storeTaskManager();
        model.addTask(toAdd);
        
        // Display reminder message only when reminder is set
        if (toAdd.getRemindDate() == null) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), true);
        } else {
            return new CommandResult(
                    String.format(MESSAGE_SUCCESS, toAdd 
                    + " Reminder on: " + toAdd.getRemindDetailsString()), true);
        }
    }

}

