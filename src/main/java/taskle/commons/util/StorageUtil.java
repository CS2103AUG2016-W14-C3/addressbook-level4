package taskle.commons.util;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import taskle.commons.core.Config;
import taskle.commons.core.EventsCenter;
import taskle.commons.events.storage.StorageChangeRequestEvent;
import taskle.commons.exceptions.DataConversionException;
import taskle.model.ReadOnlyTaskManager;
import taskle.storage.XmlFileStorage;

//@@author A0140047U
//Manage changes in directory of storage file
public class StorageUtil {
    
    private static Stack<Config> configHistory = new Stack<Config>();
    private static Stack<Config> redoConfigHistory = new Stack<Config>();
    
    public static boolean updateDirectory(File selectedDirectory) {
        assert selectedDirectory != null;
        try {
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()).delete();
            new File(config.getTaskManagerFilePath()).renameTo(new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()));
            config.setTaskManagerFileDirectory(selectedDirectory.getAbsolutePath());
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
            EventsCenter.getInstance().post(new StorageChangeRequestEvent(config.getTaskManagerFilePath(),null));
            return true;
        } catch (IOException | DataConversionException e) {
            return false;
        }
    }
    
    public static boolean updateFile(File selectedFile) {
        assert selectedFile != null;
        ReadOnlyTaskManager newTaskManager;
        try {
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            newTaskManager = XmlFileStorage.loadDataFromSaveFile(selectedFile);
            config.setTaskManagerFileDirectory(splitFilePath(selectedFile.getAbsolutePath())[0]);
            config.setTaskManagerFileName(splitFilePath(selectedFile.getAbsolutePath())[1]);
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
            EventsCenter.getInstance().post(new StorageChangeRequestEvent(config.getTaskManagerFilePath(), newTaskManager));
            return true;
        } catch (IOException | DataConversionException e) {
            return false;
        }
    }
    
    public static String[] splitFilePath(String filePath) {
        assert filePath != null;
        String[] separatedFilePath = new String[2];
        separatedFilePath[0] = filePath.substring(0, filePath.lastIndexOf(File.separator));
        separatedFilePath[1] = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        return separatedFilePath;
    }
    
    public static void storeConfig(boolean isStorageOperation) throws DataConversionException {
        System.out.println(isStorageOperation);
        if (isStorageOperation) {
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            configHistory.push(config);
        } else {
            redoConfigHistory.clear();
            configHistory.push(null);
        }
    }
    
    public static boolean restoreConfig() throws DataConversionException {
        if (configHistory.isEmpty()) {
            return false;
        }
        Config originalConfig = configHistory.pop();
        Config currentConfig = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        redoConfigHistory.push(currentConfig);
        
        if (originalConfig == null) {
            redoConfigHistory.push(null);
            return false;
        } else if (originalConfig.getTaskManagerFileName().equals(currentConfig.getTaskManagerFileName())) {
            updateDirectory(new File(originalConfig.getTaskManagerFileDirectory()));
        } else {
            updateFile(new File(originalConfig.getTaskManagerFilePath()));
        }
        return true;
    }
    
    public static boolean revertConfig() throws DataConversionException {
        if (redoConfigHistory.isEmpty()) {
            return false;
        }
        Config redoConfig = redoConfigHistory.pop();
        Config currentConfig = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        configHistory.push(currentConfig);
        
        if (redoConfig == null) {
            configHistory.push(null);
            return false;
        } else if (redoConfig.getTaskManagerFileName().equals(currentConfig.getTaskManagerFileName())) {
            updateDirectory(new File(redoConfig.getTaskManagerFileDirectory()));
        } else {
            updateFile(new File(redoConfig.getTaskManagerFilePath()));
        }
        return true;
    }
}
