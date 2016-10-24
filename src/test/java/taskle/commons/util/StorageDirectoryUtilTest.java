package taskle.commons.util;

import java.util.logging.Level;

import org.junit.Before;
import org.junit.Rule;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();
    
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
