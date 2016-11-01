package taskle.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static taskle.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static taskle.commons.core.Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
import static taskle.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.eventbus.Subscribe;

import taskle.commons.core.EventsCenter;
import taskle.commons.core.Messages;
import taskle.commons.events.model.TaskManagerChangedEvent;
import taskle.commons.events.ui.JumpToListRequestEvent;
import taskle.commons.events.ui.ShowHelpRequestEvent;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.ExitCommand;
import taskle.logic.commands.FindCommand;
import taskle.logic.commands.HelpCommand;
import taskle.logic.commands.ListCommand;
import taskle.logic.commands.RemindCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.parser.DateParser;
import taskle.model.Model;
import taskle.model.ModelManager;
import taskle.model.ReadOnlyTaskManager;
import taskle.model.TaskManager;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.model.task.TaskComparator;
import taskle.storage.StorageManager;

public class LogicManagerTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private Model model;
    private Logic logic;

    //These are for checking the correctness of the events raised
    private ReadOnlyTaskManager latestSavedTaskManager;
    private boolean helpShown;
    private int targetedJumpIndex;

    @Subscribe
    private void handleLocalModelChangedEvent(TaskManagerChangedEvent abce) {
        latestSavedTaskManager = new TaskManager(abce.data);
    }

    @Subscribe
    private void handleShowHelpRequestEvent(ShowHelpRequestEvent she) {
        helpShown = true;
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent je) {
        targetedJumpIndex = je.targetIndex;
    }

    @Before
    public void setup() {
        model = new ModelManager();
        String tempTaskManagerFile = saveFolder.getRoot().getPath() + "TempTaskManager.xml";
        String tempPreferencesFile = saveFolder.getRoot().getPath() + "TempPreferences.json";
        logic = new LogicManager(model, new StorageManager(tempTaskManagerFile, tempPreferencesFile));
        EventsCenter.getInstance().registerHandler(this);

        latestSavedTaskManager = new TaskManager(model.getTaskManager()); // last saved assumed to be up to date before.
        helpShown = false;
        targetedJumpIndex = -1; // non yet
    }

    @After
    public void teardown() {
        EventsCenter.clearSubscribers();
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Both the 'task manager' and the 'last shown list' are expected to be empty.
     * @see #assertCommandBehavior(String, String, ReadOnlyTaskManager, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, new TaskManager(), Collections.emptyList());
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the LogicManager object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *      - the backing list shown by UI matches the {@code shownList} <br>
     *      - {@code expectedAddressBook} was saved to the storage file. <br>
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage,
                                       ReadOnlyTaskManager expectedTaskManager,
                                       List<? extends ReadOnlyTask> expectedShownList) throws Exception {

        //Execute the command
        CommandResult result = logic.execute(inputCommand);

        //Confirm the ui display elements should contain the right data
        assertEquals(expectedMessage, result.getFeedback());
        assertEquals(expectedShownList, model.getFilteredTaskList());

        //Confirm the state of data (saved and in-memory) is as expected
        assertEquals(expectedTaskManager, model.getTaskManager());
        assertEquals(expectedTaskManager, latestSavedTaskManager);
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandBehavior(unknownCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_help() throws Exception {
        assertCommandBehavior("help", HelpCommand.SHOWING_HELP_MESSAGE);
        assertTrue(helpShown);
    }

    @Test
    public void execute_exit() throws Exception {
        assertCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    @Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        model.addTask(helper.generateTask(1));
        model.addTask(helper.generateTask(2));
        model.addTask(helper.generateTask(3));

        assertCommandBehavior("clear", ClearCommand.MESSAGE_SUCCESS, new TaskManager(), Collections.emptyList());
    }

    //@@author A0141780J
    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.adam();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());

    }
    //@@author A0139402M
    @Test
    public void execute_addFloatTaskWithReminder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.homeworkWithReminder();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_FLOAT_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    @Test
    public void execute_addEventWithDates_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_EVENT_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addEventWithDatesAndReminder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExamsWithReminder();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_EVENT_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedAB,
                expectedAB.getTaskList());
    }
    //@@author 

    @Test
    public void execute_addDeadlineWithDates_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_DEADLINE_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addDeadlineWithDatesAndReminders_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignmentWithReminder();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_DEADLINE_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addEventTmr_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.tutorialTmr();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_TMR_SUCCESSFUL_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addGardensByBay_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GARDENS_BY_BAY,
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addEventOnSingleDate_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.newYearDay();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_NEW_YEAR_DAY,
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addFloatTaskWithDelimiter_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    //@@author A0139402M
    @Test
    public void execute_addFloatTaskReminderMorethanOneDate_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.adam();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_BUY_GROCERIES_WITH_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    @Test
    public void execute_addDeadlineTaskReminderMorethanOneDate_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignmentWithReminder();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_NEW_YEAR_DAY_WITH_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    @Test
    public void execute_addEventTaskReminderMorethanOneDate_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.getDocsFromBobWithReminder();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GET_DOCS_FROM_BOB_WITH_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedAB,
                expectedAB.getTaskList());

    }
    //@@author 

    @Test
    public void execute_addDeadlineTaskMorethanTwoDates_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.adam();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GET_DOCS_FROM_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedAB,
                expectedAB.getTaskList());

    }

    @Test
    public void execute_addDuplicate_allowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.adam();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());

    }
    //@@author

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIncorrectIndexFormatBehaviorForCommand(String commandWord, String expectedMessage) throws Exception {
        assertCommandBehavior(commandWord , expectedMessage); //index missing
        assertCommandBehavior(commandWord + " +1", expectedMessage); //index should be unsigned
        assertCommandBehavior(commandWord + " -1", expectedMessage); //index should be unsigned
        assertCommandBehavior(commandWord + " 0", expectedMessage); //index cannot be 0
        assertCommandBehavior(commandWord + " not_a_number", expectedMessage);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    private void assertIndexNotFoundBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
        TestDataHelper helper = new TestDataHelper();
        List<Task> taskList = helper.generateTaskList(2);

        // set AB state to 2 persons
        model.resetData(new TaskManager());
        for (Task p : taskList) {
            model.addTask(p);
        }

        assertCommandBehavior(commandWord + " 3", expectedMessage, model.getTaskManager(), taskList);
    }

    @Test
    public void execute_removeInvalidArgsFormat_errorMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("remove", expectedMessage);
    }

    @Test
    public void execute_removeIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("remove");
    }

    @Test
    public void execute_remove_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threePersons = helper.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threePersons);
        expectedAB.removeTask(threePersons.get(1));
        helper.addToModel(model, threePersons);

        assertCommandBehavior("remove 2",
                String.format(RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS, 2),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    //@@author A0139402M
    @Test
    public void execute_edit_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertCommandBehavior("edit ", expectedMessage);
    }
    
    //@@author A0139402M
    @Test
    public void execute_edit_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TestDataHelper helperTest = new TestDataHelper();
        List<Task> threePersons = helperTest.generateTaskList(3);
        List<Task> threePersonsTest = helperTest.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threePersons);
        String index = "1";
        String taskName = "Eat dinner";
        Name newName = new Name(taskName);
        Task taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(Integer.parseInt(index) - 1);
        String oldName = taskToEdit.getName().fullName;
        expectedAB.editTask(0, newName);
        helperTest.addToModel(model, threePersonsTest);
        // execute command and verify result
        assertCommandBehavior(
                helperTest.generateEditCommand(index, taskName),
                String.format(EditCommand.MESSAGE_EDIT_TASK_SUCCESS, oldName + " -> " + taskName),
                expectedAB,
                expectedAB.getTaskList());
    }

    @Test
    public void execute_edit_duplicate() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        List<Task> threePersons = helper.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threePersons);
        String index = "1";
        String taskName = "Task 3";
        helper.addToModel(model, threePersons);
        expectedAB.editTask(1, new Name(taskName));
        // execute command and verify result
        assertCommandBehavior(
                helper.generateEditCommand(index, taskName),
                String.format(EditCommand.MESSAGE_EDIT_TASK_SUCCESS, "Task 1" + " -> " + taskName),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_reschedule_invalid_command() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule task", expectedMessage);
    }
    
    @Test
    public void execute_reschedule_invalid_index() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule -1", expectedMessage);
    }
    
    @Test
    public void execute_reschedule_with_no_date() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule 1 no date", 
                expectedMessage,
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_reschedule_with_more_than_2_dates() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule 1 from 17 Oct to 18 Oct to 19 Oct", 
                expectedMessage,
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_reschedule_to_float_task_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String index = "1";
        int offsetIndex = Integer.parseInt(index) - 1;

        Task taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedAB.editTaskDate(offsetIndex, null);
        Task editedTask = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " clear",
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_reschedule_to_deadline_task_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String index = "1";
        String dateTime = "13 dec 7pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedAB.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_reschedule_to_event_task_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        
        model.addTask(toBeAdded);
        
        String index = "1";
        String dateTime = "13 dec 7pm to 18 dec 10am";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedAB.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_remind_invalid_command() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind task", expectedMessage);
    }
    
    @Test
    public void execute_remind_invalid_index() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind -1", expectedMessage);
    }
    
    @Test
    public void execute_remind_invalid_date() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        model.addTask(toBeAdded);
        
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind 1 asdf", expectedMessage, expectedAB, expectedAB.getTaskList());
    }
    
    @Test
    public void execute_remind_more_than_1_date() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);
        model.addTask(toBeAdded);
        
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind 1 14 Oct 5pm to 6pm", expectedMessage, expectedAB, expectedAB.getTaskList());
    }
    
    @Test
    public void execute_remind_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TestDataHelper helperTest = new TestDataHelper();
        List<Task> threePersons = helperTest.generateTaskList(3);
        List<Task> threePersonsTest = helperTest.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threePersons);
        String index = "1";
        String dateTime = "13 dec 7pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        
        List<Date> dates = DateParser.parse(dateTime);
        Task taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getRemindDetailsString();
        String name = taskToEdit.getName().fullName;
        
        helperTest.addToModel(model, threePersonsTest);
        expectedAB.editTaskRemindDate(offsetIndex, dates.get(0));
        Task editedTask = expectedAB.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getRemindDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "remind " + index + " " + dateTime,
                String.format(RemindCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedAB,
                expectedAB.getTaskList());
    }
    //@@author 

    //@@author A0141780J
    @Test
    public void execute_findInvalidArgs_returnInvalidCommand() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }

    @Test
    public void execute_find_onlyMatchesFullWordsInNames() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p1 = helper.generateTaskWithName("KE Y");
        Task p2 = helper.generateTaskWithName("KEYKEYKEY sduauo");

        List<Task> fourPersons = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        TaskManager expectedAB = helper.generateTaskManager(fourPersons);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2);
        helper.addToModel(model, fourPersons);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }

    @Test
    public void execute_find_isNotCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTaskWithName("bla bla KEY bla");
        Task p2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p3 = helper.generateTaskWithName("key key");
        Task p4 = helper.generateTaskWithName("KEy sduauo");

        List<Task> fourPersons = helper.generateTaskList(p3, p1, p4, p2);
        TaskManager expectedAB = helper.generateTaskManager(fourPersons);
        List<Task> expectedList = fourPersons;
        helper.addToModel(model, fourPersons);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }

    @Test
    public void execute_find_matchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla rAnDoM bla bceofeia");
        Task pTarget3 = helper.generateTaskWithName("key key");
        Task p1 = helper.generateTaskWithName("sduauo");

        List<Task> fourTasks = helper.generateTaskList(pTarget1, p1, pTarget2, pTarget3);
        TaskManager expectedAB = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2, pTarget3);
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find key rAnDoM",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }
    
    @Test
    public void execute_findPendingTask_filtersPendingTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Get fruits from supermarket");
        Task task2 = helper.generateTaskWithName("Get David a burger");
        task2.setTaskDone(true);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, 11, 01);
        Date deadlineDate = calendar.getTime();
        Task deadlineTask = new DeadlineTask(
                new Name("Get soap to wash car"), deadlineDate, new UniqueTagList());

        List<Task> allTasks = helper.generateTaskList(task1, task2, deadlineTask);
        TaskManager expectedAB = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task1, deadlineTask);
        helper.addToModel(model, allTasks);

        assertCommandBehavior("find Get -pending",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }
    
    @Test
    public void execute_listEmptyArguments_showPendingAndOverdue() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Buy groceries");
        task1.setTaskDone(true);
        Task task2 = helper.generateTaskWithName("Do homework");
        Task task3 = helper.generateTaskWithName("Conduct meeting");
        Task task4 = helper.generateTaskWithName("Finish O levels");

        List<Task> fourTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, fourTasks);
        TaskManager expectedAB = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(task2, task3, task4);

        String message = "Pending, Not Done, Overdue";
        assertCommandBehavior("list ",
                String.format(ListCommand.MESSAGE_LIST_SUCCESS, message),
                expectedAB,
                expectedList);
    }
    
    @Test
    public void execute_listDoneOverdue_showsDoneAndOverdueOnly() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Buy groceries");
        task1.setTaskDone(true);
        Task task2 = helper.generateTaskWithName("Do homework");
        Task task3 = helper.generateTaskWithName("Conduct meeting");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2010, 11, 01);
        Date deadlineDate = calendar.getTime();
        DeadlineTask task4 = new DeadlineTask(
                new Name("Finish O levels"), 
                deadlineDate, 
                new UniqueTagList());

        List<Task> allTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, allTasks);
        TaskManager expectedAB = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task1, task4);

        String message = "Not Pending, Done, Overdue";
        assertCommandBehavior("list -done -overdue",
                String.format(ListCommand.MESSAGE_LIST_SUCCESS, message),
                expectedAB,
                expectedList);
    }
    
    @Test
    public void execute_listInvalidFlags_showsErrorWhileDisplayingOldList() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Buy groceries");
        task1.setTaskDone(true);
        Task task2 = helper.generateTaskWithName("Do homework");
        Task task3 = helper.generateTaskWithName("Conduct meeting");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2010, 11, 01);
        Date deadlineDate = calendar.getTime();
        DeadlineTask task4 = new DeadlineTask(
                new Name("Finish O levels"), 
                deadlineDate, 
                new UniqueTagList());

        List<Task> allTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, allTasks);
        TaskManager expectedAB = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task2, task3, task4);

        assertCommandBehavior("list -easy",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, 
                              ListCommand.MESSAGE_USAGE),
                expectedAB,
                expectedList);
    }
    //@@author
    

    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{
        
        //@@author A0141780J
        private final Calendar CALENDAR = Calendar.getInstance();
        private final String ADD_SUCCESSFUL_FLOAT_REMINDER = " remind 12 sep 2016 10am";
        private final String ADD_SUCCESSFUL_EVENT_DATE = " from 12 sep 2016 10am to 12 sep 2016 1pm";
        private final String ADD_SUCCESSFUL_EVENT_REMINDER = " from 12 sep 2016 10am to 12 sep 2016 1pm remind 10 sep 2016 10am";
        private final String ADD_SUCCESSFUL_DEADLINE_DATE = " by 31st Dec 2016 2359hours";
        private final String ADD_SUCCESSFUL_DEADLINE_REMINDER = " by 31st Dec 2016 2359hours remind 29th Dec 2016 2359hours";
        private final String ADD_TMR_SUCCESSFUL_DATE = " from tmr 1 to 2pm";
        private final String ADD_COMMAND_GARDENS_BY_BAY = 
                "add Gardens by the Bay outing from 12pm to 2pm 3 December";
        private final String ADD_COMMAND_NEW_YEAR_DAY = 
                "add New Year Day from 1 jan 2017";
        private final String ADD_COMMAND_NEW_YEAR_DAY_WITH_INVALID_REMINDER = 
                "add New Year Day from 1 jan 2017 remind 31 dec 2016 5pm to 31 dec 2016 6pm";
        private final String ADD_COMMAND_GET_DOCS_FROM_BOB = 
                "add Get documents from Bob by 14 Apr to 15 Apr";
        private final String ADD_COMMAND_GET_DOCS_FROM_BOB_WITH_INVALID_REMINDER = 
                "add Get documents from Bob by 14 Apr to 15 Apr remind 13 Apr to 14 Apr";
        private final String ADD_BUY_GROCERIES_WITH_INVALID_REMINDER = 
                "add Buy groceries remind 13 Apr 5pm to 13 Apr 6pm";
        UniqueTagList stubTagList = new UniqueTagList();

        FloatTask adam() throws Exception {
            Name name = new Name("Adam Brown");
            return new FloatTask(name, stubTagList);
        }
        
        
        FloatTask homeworkWithReminder() throws Exception {
            Name name = new Name("Do homework for CS2103T");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new FloatTask(name, remindDate, stubTagList);
        }
        
        EventTask finalExams() throws Exception {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, stubTagList);
        }
        
        EventTask finalExamsWithReminder() throws Exception {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 10, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate, stubTagList);
        }
        
        EventTask getDocsFromBobWithReminder() throws Exception {
            Name name = new Name("Get documents from Bob");
            CALENDAR.set(2016, 3, 14, 00, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 3, 15, 00, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2016, 3, 13, 00, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate, stubTagList);
        }
        
        
        DeadlineTask finishAssignment() throws Exception {
            Name name = new Name("Finish Assignment");
            CALENDAR.set(2016, 11, 31, 23, 59, 00);
            Date byDate = CALENDAR.getTime();
            return new DeadlineTask(name, byDate, stubTagList);
        }
        
        DeadlineTask finishAssignmentWithReminder() throws Exception {
            Name name = new Name("Finish Assignment");
            CALENDAR.set(2016, 11, 31, 23, 59, 00);
            Date byDate = CALENDAR.getTime();
            CALENDAR.set(2016, 11, 29, 23, 59, 00);
            Date remindDate = CALENDAR.getTime();
            return new DeadlineTask(name, byDate, remindDate, stubTagList);
        }
        
        
        EventTask tutorialTmr() throws Exception {
            Name name = new Name("2103T tutorial");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.DATE, 1);
            
            calendar.set(Calendar.HOUR_OF_DAY, 13);
            Date startDate = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            Date endDate = calendar.getTime();
            return new EventTask(name, startDate, endDate, stubTagList);
        }
        
        EventTask gardensByTheBay() throws Exception {
            Name name = new Name("Gardens by the Bay outing");
            Calendar calendar = Calendar.getInstance();
            calendar.set(2016, 11, 3, 12, 00);
            Date startDate = calendar.getTime();
            calendar.add(Calendar.HOUR_OF_DAY, 2);
            Date endDate = calendar.getTime();
            return new EventTask(name, startDate, endDate, stubTagList);
        }
        
        EventTask newYearDay() throws Exception {
            Name name = new Name("New Year Day");
            Calendar calendar = Calendar.getInstance();
            calendar.set(2017, 0, 1, 0, 0);
            Date onDate = calendar.getTime();
            return new EventTask(name, onDate, onDate, stubTagList);
        }
        
        FloatTask getFoodFromChinatown() throws Exception {
            Name name = new Name("Get food from Chinatown");
            return new FloatTask(name, stubTagList);
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        Task generateTask(int seed) throws Exception {
            return new FloatTask(
                    new Name("Task " + seed), stubTagList);
        }

        /** Generates the correct add command based on the task given */
        String generateAddCommand(Task p) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("add ");
            cmd.append(p.getName().toString());
            return cmd.toString();
        }
        
        /** Generates the correct add command based on the task and date String given */
        String generateAddCommandWithDate(Task p, String dateString) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("add ");
            cmd.append(p.getName().toString());
            cmd.append(dateString);
            return cmd.toString();
        }
        //@@author
        
        String generateEditCommand(String index, String newName) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("edit ");
            cmd.append(index).append(" ").append(newName);
            return cmd.toString();
        }
        
        /**
         * Generates an TaskManager with auto-generated task.
         */
        TaskManager generateTaskManager(int numGenerated) throws Exception{
            TaskManager taskManager = new TaskManager();
            addToTaskManager(taskManager, numGenerated);
            return taskManager;
        }

        /**
         * Generates an TaskManager based on the list of Tasks given.
         */
        TaskManager generateTaskManager(List<Task> tasks) throws Exception{
            TaskManager taskManager = new TaskManager();
            addToTaskManager(taskManager, tasks);
            return taskManager;
        }

        /**
         * Adds auto-generated Task objects to the given TaskManager
         * @param taskManager The TaskManager to which the Tasks will be added
         */
        void addToTaskManager(TaskManager taskManager, int numGenerated) throws Exception{
            addToTaskManager(taskManager, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given TaskManager
         */
        void addToTaskManager(TaskManager taskManager, List<Task> tasksToAdd) throws Exception{
            for(Task p: tasksToAdd){
                taskManager.addTask(p);
            }
        }

        /**
         * Adds auto-generated Task objects to the given model
         * @param model The model to which the Tasks will be added
         */
        void addToModel(Model model, int numGenerated) throws Exception{
            addToModel(model, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given model
         */
        void addToModel(Model model, List<Task> tasksToAdd) throws Exception{
            for(Task p: tasksToAdd){
                model.addTask(p);
            }
        }

        /**
         * Generates a list of Tasks based on the flags.
         */
        List<Task> generateTaskList(int numGenerated) throws Exception{
            List<Task> tasks = new ArrayList<>();
            for(int i = 1; i <= numGenerated; i++){
                tasks.add(generateTask(i));
            }
            tasks.sort(new TaskComparator());
            return tasks;
        }

        List<Task> generateTaskList(Task... tasks) {
            List<Task> taskList = Arrays.asList(tasks);
            taskList.sort(new TaskComparator());
            return taskList;
        }

        /**
         * Generates a Task object with given name. Other fields will have some dummy values.
         */
        Task generateTaskWithName(String name) throws Exception {
            return new FloatTask(
                    new Name(name), stubTagList);
        }
    }
}
