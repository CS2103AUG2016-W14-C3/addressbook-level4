package taskle.commons.util;

import static org.junit.Assert.assertEquals;
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

public class StorageDirectoryUtilTest {

    private Model model;
    private Logic logic;
    private Config config;
    
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
        StorageDirectoryUtil.updateDirectory(getTypicalConfig(), logic, null);
    }
    
    //Change to a valid directory - Successfully Update
    @Test
    public void updateDirectory_validDirectory_directoryChanged() {
        StorageDirectoryUtil.updateDirectory(config, logic, new File(TEST_DATA_FOLDER));
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
    }
    
    //Change to a null file - Assertion Error
    @Test
    public void updateFile_nullFile_assertionError() {
        thrown.expect(AssertionError.class);
        StorageDirectoryUtil.updateFile(config, logic, null);
    }
    
    //Change to file of invalid format - Shows Exception
    @Test
    public void updateFile_invalidFileFormat_Exception() {
        StorageDirectoryUtil.updateFile(config, logic, INVALID_FILE);
    }
    
    //Change to a valid file - Successfully Update
    @Test
    public void updateFile_validFile_FileChanged() {
        StorageDirectoryUtil.updateFile(config, logic, VALID_FILE);
        assertTrue(config.getTaskManagerFileDirectory().contains(TEST_DATA_FOLDER.substring(0, TEST_DATA_FOLDER.length() - 1)));
        assertEquals(config.getTaskManagerFileName(), "ValidFormatTaskManager.xml");
    }    
    
    @Before
    public void setup() {
        model = new ModelManager();
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
