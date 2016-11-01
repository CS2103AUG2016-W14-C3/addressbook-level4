package taskle.logic.commands;

import java.util.Date;
import java.util.List;

import taskle.commons.exceptions.IllegalValueException;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;

//@@author A0141780J

/**
 * Adds a task to the Task Manager.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

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
        this.toAdd = new FloatTask(new Name(name));
    }
    
    /**
     * Convenience constructor with name and reminder
     * @param name
     * @throws IllegalValueException
     */
    public AddCommand(String name, List<Date> remindDate)
            throws IllegalValueException {
        assert remindDate != null;
        assert remindDate.size() != 0;
        Date reminderDate = remindDate.get(0);
        this.toAdd = new FloatTask(new Name(name), reminderDate);
    }
    
    /**
     * Convenience constructor using raw name 
     * and DateTime object for deadline date. 
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name, Date deadlineDate)
            throws IllegalValueException {
        assert deadlineDate != null;
        this.toAdd = new DeadlineTask(new Name(name), deadlineDate);
    }
    
    /**
     * Convenience constructor using name,
     * date for deadline and reminder
     * @param name
     * @param deadlineDate
     * @param remindDate
     * @throws IllegalValueException
     */
    public AddCommand(String name, Date deadlineDate, List<Date> remindDate)
            throws IllegalValueException {
        assert remindDate != null;
        assert deadlineDate != null;
        assert remindDate.size() == 1;
        Date reminderDate = remindDate.get(0);
        this.toAdd = new DeadlineTask(new Name(name), deadlineDate, reminderDate);
    }
    
    
    /**
     * Convenience constructor using raw name 
     * and DateTime objects for start and end dates.
     * 
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name, Date startDate, Date endDate)
            throws IllegalValueException {
        assert startDate != null;
        assert endDate != null;
        this.toAdd = new EventTask(new Name(name), startDate, endDate);
    }

    public AddCommand(String name, Date startDate, Date endDate, List<Date> remindDate)
            throws IllegalValueException {
        assert startDate != null;
        assert endDate != null;
        assert remindDate != null;
        assert remindDate.size() == 1;
        Date reminderDate = remindDate.get(0);
        this.toAdd = new EventTask(new Name(name), startDate, endDate, reminderDate);
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
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd 
                    + " Reminder on: " + toAdd.getRemindDetailsString()), true);
        }
        
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}

