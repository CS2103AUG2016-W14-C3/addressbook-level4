package guitests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.testfx.api.FxToolkit;

import guitests.guihandles.CommandBoxHandle;
import guitests.guihandles.MainGuiHandle;
import guitests.guihandles.MainMenuHandle;
import guitests.guihandles.NotificationPaneHandle;
import guitests.guihandles.PopOverHandle;
import guitests.guihandles.StatusDisplayPanelHandle;
import guitests.guihandles.TaskCardHandle;
import guitests.guihandles.TaskListPanelHandle;
import javafx.stage.Stage;
import taskle.TestApp;
import taskle.commons.core.EventsCenter;
import taskle.model.TaskManager;
import taskle.model.task.ReadOnlyTask;
import taskle.testutil.TestUtil;
import taskle.testutil.TypicalTestTasks;

/**
 * A GUI Test class for TaskManager.
 */
public abstract class TaskManagerGuiTest {

    /* The TestName Rule makes the current test name available inside test methods */
    @Rule
    public TestName name = new TestName();

    TestApp testApp;

    protected TypicalTestTasks td = new TypicalTestTasks();

    /*
     *   Handles to GUI elements present at the start up are created in advance
     *   for easy access from child classes.
     */
    protected MainGuiHandle mainGui;
    protected MainMenuHandle mainMenu;
    protected TaskListPanelHandle taskListPanel;
    protected CommandBoxHandle commandBox;
    protected NotificationPaneHandle notificationPane;
    protected StatusDisplayPanelHandle statusDisplayPanel;
    protected PopOverHandle popOver;
    private Stage stage;

    @BeforeClass
    public static void setupSpec() {
        try {
            FxToolkit.registerPrimaryStage();
            FxToolkit.hideStage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() throws Exception {
        FxToolkit.setupStage((stage) -> {
            mainGui = new MainGuiHandle(new GuiRobot(), stage);
            mainMenu = mainGui.getMainMenu();
            taskListPanel = mainGui.getTaskListPanel();
            commandBox = mainGui.getCommandBox();
            notificationPane = mainGui.getNotificationPane();
            popOver = mainGui.getPopOver();
            statusDisplayPanel = mainGui.getStatusDisplayPanel();
            this.stage = stage;
        });
        EventsCenter.clearSubscribers();
        testApp = (TestApp) FxToolkit.setupApplication(() -> new TestApp(this::getInitialData, getDataFileLocation()));
        FxToolkit.showStage();
        while (!stage.isShowing());
        mainGui.focusOnMainApp();
    }

    /**
     * Override this in child classes to set the initial local data.
     * Return null to use the data in the file specified in {@link #getDataFileLocation()}
     */
    protected TaskManager getInitialData() {
        TaskManager tm = TestUtil.generateEmptyTaskManager();
        td.loadTaskManagerWithSampleData(tm);
        return tm;
    }

    /**
     * Override this in child classes to set the data file location.
     * @return
     */
    protected String getDataFileLocation() {
        return TestApp.SAVE_LOCATION_FOR_TESTING;
    }

    @After
    public void cleanup() throws TimeoutException {
        mainGui.closeWindow();
        FxToolkit.cleanupStages();
    }

    /**
     * Asserts the task shown in the card is same as the given task.
     * 
     * @param task given task to compare
     * @param TaskCardHandle given card to compare
     */
    public void assertMatching(ReadOnlyTask task, TaskCardHandle card) {
        assertTrue(TestUtil.compareCardAndTask(card, task));
    }

    /**
     * Asserts the size of the task list is equal to the given number.
     * 
     * @param size expected size of list
     */
    protected void assertListSize(int size) {
        int numberOfTask = taskListPanel.getNumberOfTask();
        assertEquals(size, numberOfTask);
    }

    /**
     * Asserts the message shown in the notification bar area is same as the given string.
     * 
     * @param expected expected message
     */
    protected void assertSuccessfulMessage(String expected) {
        assertEquals(expected, notificationPane.getText());
    }
    
    /**
     * Asserts that popover text is as expected.
     * 
     * @param expected expected text
     */
    protected void assertUnsuccessfulMessage(String expected) {
        assertEquals(expected, popOver.getText());
    }
    
    /**
     * Asserts that status display panel is showing the 
     * correct chips.
     * 
     * @param expected expected
     */
    protected void assertShownStatuses(
            boolean isPendingShown, boolean isDoneShown, 
            boolean isOverdueShown) {
        if (isPendingShown && isDoneShown && isOverdueShown) {
            assertTrue(statusDisplayPanel.isAllShown());
            return;
        }
        
        assertTrue(isPendingShown == statusDisplayPanel.isPendingShown());
        assertTrue(isDoneShown == statusDisplayPanel.isDoneShown());
        assertTrue(isOverdueShown == statusDisplayPanel.isOverdueShown());
    }
}
