package taskle.logic.commands;

import java.util.Date;

import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;

// Adds a task to the Task Manager.
public class AddCommand extends Command {
    //@@author A0141780J

    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_WORD_SHORT = "a";

    public static final String MESSAGE_USAGE = "\n" + COMMAND_WORD + ": Adds a task / event into Taskle (with its respective deadline or end-date.)\n"
            + "\nFormat: add task_name by [date + time] [remind date + time]\n"
            + "or\nadd task_name from [date + time] to [date + time] [remind date + time]\n"
            + "\nExample: " + "add Business Trip from 4 Oct to 5 Oct remind 3 Oct 2pm";

    public static final String MESSAGE_SUCCESS = "Added New Task: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in Taskle!";

    private final Task toAdd;

    /**
     * Convenience constructor using raw name value.
     * 
     * @param name Name of  task to be added
     */
    public AddCommand(String name) {
        assert (name != null);
        this.toAdd = new FloatTask(new Name(name));
    }
    
    /**
     * Convenience constructor using raw name value.
     * 
     * @param nameString name of task in string format
     * @param startDate start date of task
     * @param endDate end date of task
     * @param remindDate reminder date of task
     */
    public AddCommand(String nameString, Date startDate, 
                      Date endDate, Date remindDate) {
        assert (nameString != null);
        Name name = new Name(nameString);
        if (startDate != null && endDate != null) {
            toAdd = new EventTask(name, startDate, endDate);
        } else if (endDate != null) {
            toAdd = new DeadlineTask(name, endDate);
        } else {
            toAdd = new FloatTask(name);
        }
        
        if (remindDate != null) {
            toAdd.setRemindDate(remindDate);
        }
    }
    
    @Override
    public CommandResult execute() {
        assert model != null;        
        model.storeTaskManager(COMMAND_WORD);
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

