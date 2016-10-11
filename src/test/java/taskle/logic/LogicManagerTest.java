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
import taskle.logic.commands.RemoveCommand;
import taskle.model.Model;
import taskle.model.ModelManager;
import taskle.model.ReadOnlyTaskManager;
import taskle.model.TaskManager;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.ModifiableTask;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
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
        assertEquals(expectedMessage, result.feedbackToUser);
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
    
    @Test
    public void execute_addEventWithDates_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        EventTask toBeAdded = helper.finalExams();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddEventCommand(toBeAdded, 
                        helper.ADD_SUCCESSFUL_EVENT_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_addDeadlineWithDates_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        DeadlineTask toBeAdded = helper.finishAssignment();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddDeadlineCommand(toBeAdded, 
                        helper.ADD_SUCCESSFUL_DEADLINE_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
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
                helper.generateAddEventCommand(toBeAdded, 
                        helper.ADD_TMR_SUCCESSFUL_DATE),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
    }

    @Test
    public void execute_addDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        FloatTask toBeAdded = helper.adam();
        TaskManager expectedAB = new TaskManager();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task manager

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                AddCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());

    }

    @Test
    public void execute_list_showsAllTasks() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        TaskManager expectedAB = helper.generateTaskManager(2);
        List<? extends ReadOnlyTask> expectedList = expectedAB.getTaskList();

        // prepare address book state
        helper.addToModel(model, 2);

        assertCommandBehavior("list",
                ListCommand.MESSAGE_SUCCESS,
                expectedAB,
                expectedList);
    }


    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
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
        List<Task> personList = helper.generateTaskList(2);

        // set AB state to 2 persons
        model.resetData(new TaskManager());
        for (Task p : personList) {
            model.addTask(p);
        }

        assertCommandBehavior(commandWord + " 3", expectedMessage, model.getTaskManager(), personList);
    }

    @Test
    public void execute_removeInvalidArgsFormat_errorMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("remove", expectedMessage);
    }

    @Test
    public void execute_deleteIndexNotFound_errorMessageShown() throws Exception {
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
                String.format(RemoveCommand.MESSAGE_DELETE_TASK_SUCCESS, threePersons.get(1)),
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_edit_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertCommandBehavior("edit ", expectedMessage);
    }
    
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
        ModifiableTask taskToEdit = expectedAB.getUniqueTaskList().getInternalList().get(Integer.parseInt(index) - 1);
        String oldName = taskToEdit.getName().fullName;
        expectedAB.editTask(taskToEdit, newName);
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
        Name newName = new Name(taskName);
        helper.addToModel(model, threePersons);
        // execute command and verify result
        assertCommandBehavior(
                helper.generateEditCommand(index, taskName),
                EditCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());
    }
    
    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
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


    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{
        
        private final Calendar CALENDAR = Calendar.getInstance();
        private final String ADD_SUCCESSFUL_EVENT_DATE = " from 12 sep 2016 10am to 12 sep 2016 1pm";
        private final String ADD_SUCCESSFUL_DEADLINE_DATE = " by 31st Dec 2016 2359hours";
        private final String ADD_TMR_SUCCESSFUL_DATE = " from tmr 1 to 2pm";
        
        UniqueTagList stubTagList = new UniqueTagList();

        FloatTask adam() throws Exception {
            Name name = new Name("Adam Brown");
            return new FloatTask(name, stubTagList);
        }
        
        EventTask finalExams() throws Exception {
            Name name = new Name("Final Exams");
            CALENDAR.set(2016, 8, 12, 10, 00, 00);
            Date startDate = CALENDAR.getTime();
            CALENDAR.set(2016, 8, 12, 13, 00, 00);
            Date endDate = CALENDAR.getTime();
            return new EventTask(name, startDate, endDate, stubTagList);
        }
        
        DeadlineTask finishAssignment() throws Exception {
            Name name = new Name("Finish Assignment");
            CALENDAR.set(2016, 11, 31, 23, 59, 00);
            Date byDate = CALENDAR.getTime();
            return new DeadlineTask(name, byDate, stubTagList);
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

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the person data field values
         */
        Task generateTask(int seed) throws Exception {
            return new FloatTask(
                    new Name("Task " + seed), stubTagList);
        }

        /** Generates the correct add float command based on the task given */
        String generateAddCommand(FloatTask p) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("add ");
            cmd.append(p.getName().toString());
            return cmd.toString();
        }
        
        /** Generates the correct add event command based on the task given */
        String generateAddEventCommand(EventTask p, String dateString) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("add ");
            cmd.append(p.getName().toString());
            cmd.append(dateString);
            return cmd.toString();
        }
        
        /** Generates the correct add deadline command based on the task given */
        String generateAddDeadlineCommand(DeadlineTask p, String dateString) {
            StringBuffer cmd = new StringBuffer();
            cmd.append("add ");
            cmd.append(p.getName().toString());
            cmd.append(dateString);
            return cmd.toString();
        }
        
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
            return tasks;
        }

        List<Task> generateTaskList(Task... tasks) {
            return Arrays.asList(tasks);
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
