package taskle.logic.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import taskle.commons.exceptions.IllegalValueException;
import taskle.logic.history.History;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;

/**
 * Adds a task to the Task Manager.
 */
public class AddCommand extends Command {
    /** 
     * stub unique tag list used for every add commands for now
     */
    UniqueTagList stubTagList = new UniqueTagList();

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task to the Task Manager."
            + "Format: add task_name [by date & time] [remind date time]"
            + " or add task_name [from date & time] [to date & time] [remind date time]"
            + "Example: " + COMMAND_WORD
            + " add Business Trip from 4 Oct to 5 Oct remind 3 Oct 2pm";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the Task Manager";

    private final Task toAdd;

    /**
     * Convenience constructor using raw name value.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name)
            throws IllegalValueException {
        this.toAdd = new FloatTask(new Name(name), stubTagList);
    }
    
    /**
     * Convenience constructor with name and reminder
     * @param name
     * @throws IllegalValueException
     */
    public AddCommand(String name, List<Date> remindDate)
            throws IllegalValueException {
        assert remindDate != null;
        assert remindDate.size() == 1;
        Date reminderDate = remindDate.get(0);
        this.toAdd = new FloatTask(new Name(name), reminderDate, stubTagList);
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
        this.toAdd = new DeadlineTask(new Name(name), deadlineDate, stubTagList);
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
        this.toAdd = new DeadlineTask(new Name(name), deadlineDate, reminderDate, stubTagList);
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
        this.toAdd = new EventTask(new Name(name), startDate, endDate, stubTagList);
    }

    public AddCommand(String name, Date startDate, Date endDate, List<Date> remindDate)
            throws IllegalValueException {
        assert startDate != null;
        assert endDate != null;
        assert remindDate != null;
        assert remindDate.size() == 1;
        Date reminderDate = remindDate.get(0);
        this.toAdd = new EventTask(new Name(name), startDate, endDate, reminderDate, stubTagList);
    }
    

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addTask(toAdd);
            tasksAffected = new ArrayList<Task>();
            tasksAffected.add(toAdd);
            History.insert(this);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (UniqueTaskList.DuplicateTaskException e) {
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
        }

    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

}

