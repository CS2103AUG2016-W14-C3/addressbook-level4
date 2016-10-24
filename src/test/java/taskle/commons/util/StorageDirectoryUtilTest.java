package taskle.commons.util;

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
