package taskle.storage;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import taskle.commons.events.model.TaskManagerChangedEvent;
import taskle.commons.events.storage.DataSavingExceptionEvent;
import taskle.commons.events.storage.StorageChangedEvent;
import taskle.model.ReadOnlyTaskManager;
import taskle.model.TaskManager;
import taskle.model.UserPrefs;
import taskle.storage.JsonTaskPrefsStorage;
import taskle.storage.Storage;
import taskle.storage.StorageManager;
import taskle.storage.XmlTaskManagerStorage;
import taskle.testutil.EventsCollector;
import taskle.testutil.TypicalTestTasks;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StorageManagerTest {

    private StorageManager storageManager;

    private final static String TEMP_PATH = "data/taskmanager.xml";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();


    @Before
    public void setup() {
        storageManager = new StorageManager(getTempFilePath("ab"), getTempFilePath("prefs"));
    }


    private String getTempFilePath(String fileName) {
        return testFolder.getRoot().getPath() + fileName;
    }


    /*
     * Note: This is an integration test that verifies the StorageManager is properly wired to the
     * {@link JsonUserPrefsStorage} class.
     * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
     */

    @Test
    public void prefsReadSave() throws Exception {
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(300, 600, 4, 6);
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void taskManagerReadSave() throws Exception {
        TaskManager original = new TypicalTestTasks().getTypicalTaskManager();
        storageManager.saveTaskManager(original);
        ReadOnlyTaskManager retrieved = storageManager.readTaskManager().get();
        assertEquals(original, new TaskManager(retrieved));
        //More extensive testing of TaskManager saving/reading is done in XmlTaskManagerStorageTest
    }

    @Test
    public void getTaskManagerFilePath(){
        assertNotNull(storageManager.getTaskManagerFilePath());
    }

    @Test
    public void handleTaskManagerChangedEvent_exceptionThrown_eventRaised() throws IOException {
        //Create a StorageManager while injecting a stub that throws an exception when the save method is called
        Storage storage = new StorageManager(new XmlTaskManagerStorageExceptionThrowingStub("dummy"), new JsonTaskPrefsStorage("dummy"));
        EventsCollector eventCollector = new EventsCollector();
        storage.handleTaskManagerChangedEvent(new TaskManagerChangedEvent(new TaskManager()));
        assertTrue(eventCollector.get(0) instanceof DataSavingExceptionEvent);
    }

    //@@author A0140047U
    //Sets TaskManager File Path based on null value - should result in assertion error
    @Test
    public void setTaskManagerFilePath_nullValue_assertionError() {
        thrown.expect(AssertionError.class);
        storageManager.setTaskManagerFilePath(null);
    }
    
    //Sets TaskManager File Path - StorageLocationChangedEvent should be raised
    @Test
    public void setTaskManagerFilePath_validPath_raiseEvent() {
        EventsCollector eventCollector = new EventsCollector();
        storageManager.setTaskManagerFilePath(TEMP_PATH);
        assertTrue(eventCollector.get(0) instanceof StorageChangedEvent);
    }

    //@@author
    /**
     * A Stub class to throw an exception when the save method is called
     */
    class XmlTaskManagerStorageExceptionThrowingStub extends XmlTaskManagerStorage{

        public XmlTaskManagerStorageExceptionThrowingStub(String filePath) {
            super(filePath);
        }

        @Override
        public void saveTaskManager(ReadOnlyTaskManager taskManager, String filePath) throws IOException {
            throw new IOException("dummy exception");
        }
    }


}
