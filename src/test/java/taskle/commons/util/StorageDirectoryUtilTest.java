package taskle.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import taskle.commons.core.Config;
import taskle.logic.Logic;
import taskle.logic.LogicManager;
import taskle.model.Model;
import taskle.model.ModelManager;
import taskle.storage.StorageManager;

//@@author A0140047U
public class StorageDirectoryUtilTest {

    private Logic logic;
    private Config config;
    
    private final static String TEST_FILE_DIRECTORY = "directory";
    private final static String TEST_FILE_FILENAME = "file.txt";
    
    private static final String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/StorageDirectoryUtilTest/");
    private static final File VALID_FILE = new File(TEST_DATA_FOLDER + "ValidFormatTaskManager.xml");
    private static final File INVALID_FILE = new File(TEST_DATA_FOLDER + "InvalidFormatTaskManager.xml");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    
    //Change to a null directory - Shows Assertion Error
    @Test
    public void updateDirectory_nullDirectory_assertionError() {
        thrown.expect(AssertionError.class);
        StorageDirectoryUtil.updateDirectory(logic, null);
    }
    
    //Change to a valid directory - Successfully Update
    @Test
    public void updateDirectory_validDirectory_directoryChanged() {
        StorageDirectoryUtil.updateDirectory(logic, new File(TEST_DATA_FOLDER));
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
    }
    
    //Change to a null file - Assertion Error
    @Test
    public void updateFile_nullFile_assertionError() {
        thrown.expect(AssertionError.class);
        StorageDirectoryUtil.updateFile(logic, null);
    }
    
    //Change to file of invalid format - Returns False
    @Test
    public void updateFile_invalidFileFormat_returnsFalse() {
        boolean isFileUpdated = StorageDirectoryUtil.updateFile(logic, INVALID_FILE);
        assertFalse(isFileUpdated);
    }
    
    //Change to a valid file - Successfully Update
    @Test
    public void updateFile_validFile_FileChanged() {
        StorageDirectoryUtil.updateFile(logic, VALID_FILE);
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
        assertEquals(config.getTaskManagerFileName(), "ValidFormatTaskManager.xml");
    }
    
    //Split a null object - Assertion Error
    @Test
    public void splitFilePath_nullFilePath_assertionError() {
        thrown.expect(AssertionError.class);
        StorageDirectoryUtil.splitFilePath(null);
    }
    
    //Split an invalid String file path format - IndexOutOfBoundsException
    @Test
    public void splitFilePath_invalidFilePath_indexOutOfBoundsException() {
        thrown.expect(IndexOutOfBoundsException.class);
        StorageDirectoryUtil.splitFilePath("");
    }
    
    //Split a valid String file path format
    @Test
    public void splitFilePath_validFilePath_filePathSuccess() {
        String[] sampleFilePath = StorageDirectoryUtil.splitFilePath(TEST_FILE_DIRECTORY + File.separator +
                TEST_FILE_FILENAME);
        assertEquals(sampleFilePath[0], TEST_FILE_DIRECTORY);
        assertEquals(sampleFilePath[1], TEST_FILE_FILENAME);
    }
    
    @Before
    public void setUp() {
        Model model = new ModelManager();
        String tempTaskManagerFile = saveFolder.getRoot().getPath() + "TempTaskManager.xml";
        String tempPreferencesFile = saveFolder.getRoot().getPath() + "TempPreferences.json";
        logic = new LogicManager(model, new StorageManager(tempTaskManagerFile, tempPreferencesFile));
        config = getTypicalConfig();
    }
    
    private Config getTypicalConfig() {
        Config config = new Config();
        config.setAppTitle("Typical App Title");
        config.setLogLevel(Level.INFO);
        config.setUserPrefsFilePath("C:\\preferences.json");
        config.setTaskManagerFileDirectory("data");
        config.setTaskManagerFileName("taskmanager.xml");
        config.setTaskManagerName("TypicalTaskManagerName");
        return config;
    }
   
}
