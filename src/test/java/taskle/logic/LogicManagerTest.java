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
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.eventbus.Subscribe;

import taskle.commons.core.EventsCenter;
import taskle.commons.core.Messages;
import taskle.commons.events.model.TaskManagerChangedEvent;
import taskle.commons.events.ui.JumpToListRequestEvent;
import taskle.commons.events.ui.ShowHelpRequestEvent;
import taskle.commons.util.TaskUtil;
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.commands.DoneCommand;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
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
     *      - the internal task manager data are same as those in the {@code expectedTaskManager} <br>
     *      - the backing list shown by UI matches the {@code shownList} <br>
     *      - {@code expectedTaskManager} was saved to the storage file. <br>
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
    public void executeAddCommand_addFloatTask_successfulTaskAdd() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.buyEggs();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    @Test
    public void executeAddCommand_addEventWithDates_successfulEventAdd() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_EVENT_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0139402M
    @Test
    public void executeAddCommand_addFloatTaskWithReminder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.homeworkWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_FLOAT_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    @Test
    public void executeAddCommand_addEventWithDatesAndReminder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExamsWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_EVENT_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeAddCommand_addEventWithDatesAndReminderAfterEndDate_returnIncorrectCommand() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        model.addTask(toBeAdded);
        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_UNSUCCESSFUL_EVENT_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                        Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0141780J
    @Test
    public void executeAddCommand_addDeadlineWithDates_successfulDeadlineAdd() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_DEADLINE_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0139402M
    @Test
    public void executeAddCommand_addDeadlineWithDatesAndReminders_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignmentWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_SUCCESSFUL_DEADLINE_REMINDER),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded + " Reminder on: " + toBeAdded.getRemindDetailsString()),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeAddCommand_addDeadlineWithDatesAndReminderAfterEndDate_returnIncorrectCommand() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        model.addTask(toBeAdded);
        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_UNSUCCESSFUL_DEADLINE_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                        Messages.MESSAGE_REMINDER_AFTER_FINAL_DATE),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0141780J
    @Test
    public void executeAddCommand_addEventTmr_successfulEventAdd() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.tutorialTmr();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommandWithDate(toBeAdded, 
                        helper.ADD_TMR_SUCCESSFUL_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeAddCommand_addTaskWithByInName_byRecognizedAsPartOfName() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GARDENS_BY_BAY,
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeAddCommand_addEventOnSingleDate_successfulEventAdd() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.newYearDay();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_NEW_YEAR_DAY,
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeAddCommand_addFloatTaskWithDelimiter_taskAddedWithDelimiter() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0139402M
    @Test
    public void executeAddCommand_addFloatTaskWithRemindInName_successful() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateTaskWithName("remind papa");
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_REMIND_PAPA,
                String.format(AddCommand.MESSAGE_SUCCESS, 
                              toBeAdded),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    @Test
    public void executeAddCommand_addDeadlineTaskReminderMorethanOneDate_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignmentWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_NEW_YEAR_DAY_WITH_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    @Test
    public void execute_addEventTaskReminderMorethanOneDate_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.getDocsFromBobWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GET_DOCS_FROM_BOB_WITH_INVALID_REMINDER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    //@@author A0141780J
    @Test
    public void executeAddCommand_addDeadlineTaskMorethanTwoDates_returnIncorrectCommand() 
            throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.buyEggs();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.ADD_COMMAND_GET_DOCS_FROM_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedTM,
                expectedTM.getTaskList());

    }

    @Test
    public void executeAddCommand_addDuplicate_allowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.buyEggs();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        expectedTM.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedTM,
                expectedTM.getTaskList());

    }
    
    @Test
    public void executeAddCommand_noArguments_returnUsageMessage() throws Exception {
        // setup expectations
        TaskManager expectedTM = new TaskManager();

        // execute command and verify result
        String command = "add";
        assertCommandBehavior(
                command,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, 
                              AddCommand.MESSAGE_USAGE),
                expectedTM,
                expectedTM.getTaskList());

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
     * targeting a single task in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIndexNotFoundBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
        TestDataHelper helper = new TestDataHelper();
        List<Task> taskList = helper.generateTaskList(2);

        // set AB state to 2 tasks
        model.resetData(new TaskManager());
        for (Task p : taskList) {
            model.addTask(p);
        }

        assertCommandBehavior(commandWord + " 3", expectedMessage, model.getTaskManager(), taskList);
    }

    @Test
    public void executeRemoveCommand_removeInvalidArgsFormat_errorMessageShown() 
            throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("remove", expectedMessage);
    }

    @Test
    public void executeRemoveCommand_removeIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("remove");
    }

    @Test
    public void executeRemoveCommand_removeAvailableIndex_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskManager expectedTM = helper.generateTaskManager(threeTasks);
        expectedTM.removeTask(threeTasks.get(1));
        helper.addToModel(model, threeTasks);

        assertCommandBehavior("remove 2",
                String.format(RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS, 2),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    //@@author A0125509H
    @Test
    public void executeDoneCommand_doneAvailableIndex_completesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Get fruits from supermarket");
        Task task2 = helper.generateTaskWithName("Get David a burger");

        List<Task> allTasks = helper.generateTaskList(task1, task2);
        List<Task> expectedTasks = helper.generateTaskList(task1.copy(), 
                task2.copy());
        List<Task> expectedList = helper.generateTaskList(task1);
        TaskManager expectedTM = helper.generateTaskManager(expectedTasks);
        expectedTM.doneTask(0, true);
        helper.addToModel(model, allTasks);

        assertCommandBehavior("done 1",
                DoneCommand.MESSAGE_DONE_TASK_SUCCESS,
                expectedTM,
                expectedList);
        
    }
    
    @Test
    public void executeDoneCommand_doneInvalidIndex_showsErrorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Get fruits from supermarket");
        Task task2 = helper.generateTaskWithName("Get David a burger");

        List<Task> allTasks = helper.generateTaskList(task1, task2);
        TaskManager expectedTM = helper.generateTaskManager(allTasks);
        helper.addToModel(model, allTasks);

        assertCommandBehavior("done 2016",
                MESSAGE_INVALID_TASK_DISPLAYED_INDEX,
                expectedTM,
                allTasks);
        
    }
    
    //@@author A0139402M
    @Test
    public void executEditCommand_invalidArgsFormat_incorrectCommand() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertCommandBehavior(EditCommand.COMMAND_WORD + " ", expectedMessage);
    }
    
    @Test
    public void executeEditCommand_validEdit_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TestDataHelper helperTest = new TestDataHelper();
        List<Task> threeTasks = helperTest.generateTaskList(3);
        List<Task> threeTasksTest = helperTest.generateTaskList(3);

        TaskManager expectedTM = helper.generateTaskManager(threeTasks);
        String index = "1";
        String taskName = "Eat dinner";
        Name newName = new Name(taskName);
        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(Integer.parseInt(index) - 1);
        String oldName = taskToEdit.getName().fullName;
        expectedTM.editTask(0, newName);
        helperTest.addToModel(model, threeTasksTest);
        // execute command and verify result
        assertCommandBehavior(
                helperTest.generateEditCommand(index, taskName),
                String.format(EditCommand.MESSAGE_EDIT_TASK_SUCCESS, oldName + " -> " + taskName),
                expectedTM,
                expectedTM.getTaskList());
    }

    @Test
    public void executeEditCommand_duplicateTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskManager expectedTM = helper.generateTaskManager(threeTasks);
        String index = "1";
        String taskName = "Task 3";
        helper.addToModel(model, threeTasks);
        expectedTM.editTask(1, new Name(taskName));
        // execute command and verify result
        assertCommandBehavior(
                helper.generateEditCommand(index, taskName),
                String.format(EditCommand.MESSAGE_EDIT_TASK_SUCCESS, "Task 1" + " -> " + taskName),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_invalidCommand_errorMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule task", expectedMessage);
    }
    
    @Test
    public void executeRescheduleCommand_invalidTaskIndex_errorMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule -1", expectedMessage);
    }
    
    @Test
    public void executeRescheduleCommand_noDateProvided_errorMessage() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule 1 no date", 
                expectedMessage,
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_moreThanTwoDates_errorMessage() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RescheduleCommand.MESSAGE_USAGE);
        assertCommandBehavior("reschedule 1 from 17 Oct to 18 Oct to 19 Oct", 
                expectedMessage,
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_eventTaskToFloatTask_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String index = "1";
        int offsetIndex = Integer.parseInt(index) - 1;

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, null);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " clear",
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_eventTaskToDeadlineTask_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String index = "1";
        String dateTime = "31 dec 11pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_eventTaskToEventTask_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.gardensByTheBay();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded.copy());
        
        model.addTask(toBeAdded);

        String index = "1";
        String dateTime = "28 dec 7pm to 9pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_deadlineTaskToFloatTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);
        
        String index = "1";
        int offsetIndex = Integer.parseInt(index) - 1;

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, null);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " clear",
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_deadlineTaskToDeadlineTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded.copy());
        
        model.addTask(toBeAdded);
        
        String index = "1";
        String dateTime = "28 dec 7am";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    
    @Test
    public void executeRescheduleCommand_deadlineTaskToEventTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);
        
        String index = "1";
        String dateTime = "13 dec 7pm to 13 dec 9pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_floatTaskToDeadlineTask_successful() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);

        String index = "1";
        String dateTime = "13 dec 7pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRescheduleCommand_floatTaskToEventTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        
        model.addTask(toBeAdded);
        
        String index = "1";
        String dateTime = "13 dec 7pm to 18 dec 10am";
        int offsetIndex = Integer.parseInt(index) - 1;
        List<Date> dates = DateParser.parse(dateTime);

        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getDetailsString();
        String name = taskToEdit.getName().fullName;
        
        expectedTM.editTaskDate(offsetIndex, dates);
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "reschedule " + index + " " + dateTime,
                String.format(RescheduleCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeRemindCommand_invalidCommand_errorMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind task", expectedMessage);
    }
    
    @Test
    public void executeRemindCommand_invalidTaskIndex_errorMessage() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind -1", expectedMessage);
    }
    
    @Test
    public void executeRemindCommand_invalidReminderDate_errorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded);
        
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind 1 asdf", expectedMessage, expectedTM, expectedTM.getTaskList());
    }
    
    @Test
    public void executeRemindCommand_moreThanOneReminderDate_errorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.getFoodFromChinatown();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded);
        
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemindCommand.MESSAGE_USAGE);
        assertCommandBehavior("remind 1 14 Oct 5pm to 6pm", expectedMessage, expectedTM, expectedTM.getTaskList());
    }
    
    @Test
    public void executeRemindCommand_validRemind_successfulSet() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TestDataHelper helperTest = new TestDataHelper();
        List<Task> threeTasks = helperTest.generateTaskList(3);
        List<Task> threeTasksTest = helperTest.generateTaskList(3);

        TaskManager expectedTM = helper.generateTaskManager(threeTasks);
        String index = "1";
        String dateTime = "13 dec 7pm";
        int offsetIndex = Integer.parseInt(index) - 1;
        
        List<Date> dates = DateParser.parse(dateTime);
        Task taskToEdit = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String oldDetails = taskToEdit.getRemindDetailsString();
        String name = taskToEdit.getName().fullName;
        
        helperTest.addToModel(model, threeTasksTest);
        expectedTM.editTaskRemindDate(offsetIndex, dates.get(0));
        Task editedTask = expectedTM.getUniqueTaskList().getInternalList().get(offsetIndex);
        String newDetails = editedTask.getRemindDetailsString();
        // execute command and verify result
        assertCommandBehavior(
                "remind " + index + " " + dateTime,
                String.format(RemindCommand.MESSAGE_EDIT_TASK_SUCCESS, name + " " + oldDetails + " -> " + newDetails),
                expectedTM,
                expectedTM.getTaskList());
    }
    
    @Test
    public void executeVerifyRemindDate_noRemindDate_returnEmptyList() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded);

        Date currentDateTime = new Date();
        List<Task> listTask= logic.verifyReminder(currentDateTime);
        assertEquals(0, listTask.size());
    }
    
    @Test
    public void executeVerifyRemindDate_oneRemindDate_returnListSizeOne() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExamsWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded);

        Date currentDateTime = new Date();
        List<Task> listTask= logic.verifyReminder(currentDateTime);
        assertEquals(1, listTask.size());
    }
    
    @Test
    public void executeDismissReminder_dismissNoReminders_returnSameNumberOfReminders() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExamsInFarFutureWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded);

        Date currentDateTime = new Date();
        logic.dismissReminder(currentDateTime);
        int numReminders = 0;
        List<ReadOnlyTask> taskList = model.getTaskManager().getTaskList();
        for(int i = 0; i < taskList.size(); i++) {
            if(!("").equals(taskList.get(i).getRemindDetailsString())) {
                numReminders++;
            }
        }
        assertEquals(1, numReminders);        
    }
    
    @Test
    public void executeDismissReminder_dismissAllReminders_returnZeroReminders() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExamsWithReminder();
        FloatTask toBeAdded2 = helper.homeworkWithReminder();
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toBeAdded2);
        expectedTM.addTask(toBeAdded);
        model.addTask(toBeAdded2);
        model.addTask(toBeAdded);

        Date currentDateTime = new Date();
        logic.dismissReminder(currentDateTime);
        int numReminders = 0;
        List<ReadOnlyTask> taskList = model.getTaskManager().getTaskList();
        for(int i = 0; i < taskList.size(); i++) {
            if(!("").equals(taskList.get(i).getRemindDetailsString())) {
                numReminders++;
            }
        }
        assertEquals(0, numReminders);        
    }
    
    

    //@@author A0141780J
    @Test
    public void executeFindCommand_findInvalidArgs_returnInvalidCommand() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }

    @Test
    public void executeFindCommand_findKeywordInMultipleTasks_onlyMatchesFullWordsInNames() 
            throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p1 = helper.generateTaskWithName("KE Y");
        Task p2 = helper.generateTaskWithName("KEYKEYKEY sduauo");

        List<Task> fourTasks = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        TaskManager expectedTM = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2);
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedTM,
                expectedList);
    }

    @Test
    public void executeFindCommand_findCaseSensitive_returnCaseSensitiveResults() 
            throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTaskWithName("bla bla KEY bla");
        Task p2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p3 = helper.generateTaskWithName("key key");
        Task p4 = helper.generateTaskWithName("KEy sduauo");

        List<Task> fourTasks = helper.generateTaskList(p3, p1, p4, p2);
        TaskManager expectedTM = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = fourTasks;
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedTM,
                expectedList);
    }

    @Test
    public void executeFindCommand_findMultipleKeywords_matchesIfAnyKeywordPresent() 
            throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla rAnDoM bla bceofeia");
        Task pTarget3 = helper.generateTaskWithName("key key");
        Task p1 = helper.generateTaskWithName("sduauo");

        List<Task> fourTasks = helper.generateTaskList(pTarget1, p1, pTarget2, pTarget3);
        TaskManager expectedTM = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2, pTarget3);
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find key rAnDoM",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeFindCommand_findPendingStatus_onlyListPendingTasks() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Get fruits from supermarket");
        Task task2 = helper.generateTaskWithName("Get David a burger");
        task2.setTaskDone(true);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, 11, 01);
        Date deadlineDate = calendar.getTime();
        Task deadlineTask = new DeadlineTask(
                new Name("Get soap to wash car"), deadlineDate);

        List<Task> allTasks = helper.generateTaskList(task1, task2, deadlineTask);
        TaskManager expectedTM = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task1, deadlineTask);
        helper.addToModel(model, allTasks);

        assertCommandBehavior("find Get -pending",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeListCommand_emptyArgs_showPendingAndOverdue() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task task1 = helper.generateTaskWithName("Buy groceries");
        task1.setTaskDone(true);
        Task task2 = helper.generateTaskWithName("Do homework");
        Task task3 = helper.generateTaskWithName("Conduct meeting");
        Task task4 = helper.generateTaskWithName("Finish O levels");

        List<Task> fourTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, fourTasks);
        TaskManager expectedTM = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(task2, task3, task4);

        String message = "Pending, Not Done, Overdue";
        assertCommandBehavior("list ",
                String.format(ListCommand.MESSAGE_LIST_SUCCESS, message),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeListCommand_listDoneOverdue_showsDoneAndOverdueOnly() 
            throws Exception {
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
                deadlineDate);

        List<Task> allTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, allTasks);
        TaskManager expectedTM = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task1, task4);

        String message = "Not Pending, Done, Overdue";
        assertCommandBehavior("list -done -overdue",
                String.format(ListCommand.MESSAGE_LIST_SUCCESS, message),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeListCommand_invalidStatusFlags_showsErrorWhileDisplayingOldList() 
            throws Exception {
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
                deadlineDate);

        List<Task> allTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, allTasks);
        TaskManager expectedTM = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task2, task3, task4);

        assertCommandBehavior("list -easy",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, 
                              ListCommand.MESSAGE_USAGE),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeShortCommand_validList_returnsList() throws Exception {
        // prepare 1 done task, 2 pending task, 1 deadline task
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
                deadlineDate);

        // Generate list from prepared tasks and add to model and expectations.
        List<Task> allTasks = helper.generateTaskList(task1, task2, task3, task4);
        helper.addToModel(model, allTasks);
        TaskManager expectedTM = helper.generateTaskManager(allTasks);
        List<Task> expectedList = helper.generateTaskList(task2, task3);

        assertCommandBehavior("l -pending",
                String.format(ListCommand.MESSAGE_LIST_SUCCESS, "Pending, Not Done, Not Overdue"),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeShortCommand_validAdd_addsSuccessfully() throws Exception {
        // prepare 1 task for add
        TestDataHelper helper = new TestDataHelper();
        Task toAdd = helper.generateTaskWithName("Buy eggs");
        
        TaskManager expectedTM = new TaskManager();
        expectedTM.addTask(toAdd);
        List<ReadOnlyTask> expectedList = expectedTM.getTaskList();

        assertCommandBehavior("a Buy eggs",
                String.format(AddCommand.MESSAGE_SUCCESS, toAdd),
                expectedTM,
                expectedList);
    }
    
    @Test
    public void executeShortCommand_validRemove_removesSuccessfully() throws Exception {
        // prepare 1 task for add
        TestDataHelper helper = new TestDataHelper();
        Task toRemove = helper.generateTaskWithName("Buy eggs");
        
        List<Task> allTasks = helper.generateTaskList(toRemove);
        
        TaskManager expectedTM = new TaskManager();
        List<ReadOnlyTask> expectedList = expectedTM.getTaskList();
        helper.addToModel(model, allTasks);

        assertCommandBehavior("rm 1",
                String.format(RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS, 1),
                expectedTM,
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
        private final String ADD_UNSUCCESSFUL_EVENT_INVALID_REMINDER = 
                "add event from 12 sep 2016 10am to 12 sep 2016 1pm remind 10 oct 2016 10am";
        private final String ADD_UNSUCCESSFUL_DEADLINE_INVALID_REMINDER = 
                "add event by 12 sep 2016 1pm remind 10 oct 2016 10am";
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
        private final String ADD_COMMAND_REMIND_PAPA = 
                "add remind papa";

        FloatTask buyEggs() {
            Name name = new Name("Buy eggs");
            return new FloatTask(name);
        }
        
        
        FloatTask homeworkWithReminder() {
            Name name = new Name("Do homework for CS2103T");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new FloatTask(name, remindDate);
        }
        
        EventTask finalExams() {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate);
        }
        
        EventTask finalExamsWithReminder() {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 10, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate);
        }
        
        EventTask finalExamsWithInvalidReminder() {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2016, 9, 10, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate);
        }
        
        EventTask finalExamsInFarFutureWithReminder() {
            Name name = new Name("Final Exams");
            CALENDAR.set(2050, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2050, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2050, 8, 10, 10, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate);
        }
        
        EventTask getDocsFromBobWithReminder() {
            Name name = new Name("Get documents from Bob");
            CALENDAR.set(2016, 3, 14, 00, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 3, 15, 00, 00, 00);
            Date endDate = CALENDAR.getTime();
            CALENDAR.set(2016, 3, 13, 00, 00, 00);
            Date remindDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, remindDate);
        }
        
        
        DeadlineTask finishAssignment() {
            Name name = new Name("Finish Assignment");
            CALENDAR.set(2016, 11, 31, 23, 59, 00);
            Date byDate = CALENDAR.getTime();
            return new DeadlineTask(name, byDate);
        }
        
        DeadlineTask finishAssignmentWithReminder() {
            Name name = new Name("Finish Assignment");
            CALENDAR.set(2016, 11, 31, 23, 59, 00);
            Date byDate = CALENDAR.getTime();
            CALENDAR.set(2016, 11, 29, 23, 59, 00);
            Date remindDate = CALENDAR.getTime();
            return new DeadlineTask(name, byDate, remindDate);
        }
        
        
        EventTask tutorialTmr() {
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
            return new EventTask(name, startDate, endDate);
        }
        
        EventTask gardensByTheBay() {
            Name name = new Name("Gardens by the Bay outing");
            Calendar calendar = Calendar.getInstance();
            calendar.set(2016, 11, 3, 12, 00);
            Date startDate = calendar.getTime();
            calendar.add(Calendar.HOUR_OF_DAY, 2);
            Date endDate = calendar.getTime();
            return new EventTask(name, startDate, endDate);
        }
        
        EventTask newYearDay() {
            Name name = new Name("New Year Day");
            Calendar calendar = Calendar.getInstance();
            calendar.set(2017, 0, 1, 23, 59, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date onDate = calendar.getTime();
            return new EventTask(name, onDate, onDate);
        }
        
        FloatTask getFoodFromChinatown() {
            Name name = new Name("Get food from Chinatown");
            return new FloatTask(name);
        }
        
        //@@author

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        Task generateTask(int seed) {
            return new FloatTask(
                    new Name("Task " + seed));
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
            cmd.append(EditCommand.COMMAND_WORD).append(" ");
            cmd.append(index).append(" ").append(newName);
            return cmd.toString();
        }
        
        /**
         * Generates an TaskManager with auto-generated task.
         */
        TaskManager generateTaskManager(int numGenerated) {
            TaskManager taskManager = new TaskManager();
            addToTaskManager(taskManager, numGenerated);
            return taskManager;
        }

        /**
         * Generates an TaskManager based on the list of Tasks given.
         */
        TaskManager generateTaskManager(List<Task> tasks) {
            TaskManager taskManager = new TaskManager();
            addToTaskManager(taskManager, tasks);
            return taskManager;
        }

        /**
         * Adds auto-generated Task objects to the given TaskManager
         * @param taskManager The TaskManager to which the Tasks will be added
         */
        void addToTaskManager(TaskManager taskManager, int numGenerated) {
            addToTaskManager(taskManager, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given TaskManager
         */
        void addToTaskManager(TaskManager taskManager, List<Task> tasksToAdd) {
            for(Task p: tasksToAdd){
                taskManager.addTask(p);
            }
        }

        /**
         * Adds auto-generated Task objects to the given model
         * @param model The model to which the Tasks will be added
         */
        void addToModel(Model model, int numGenerated) {
            addToModel(model, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given model
         */
        void addToModel(Model model, List<Task> tasksToAdd) {
            for(Task p: tasksToAdd){
                model.addTask(p);
            }
        }

        /**
         * Generates a list of Tasks based on the flags.
         */
        List<Task> generateTaskList(int numGenerated) {
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
        Task generateTaskWithName(String name) {
            return new FloatTask(
                    new Name(name));
        }
    }
}
