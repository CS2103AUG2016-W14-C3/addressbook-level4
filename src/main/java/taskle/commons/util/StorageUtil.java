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
    
    private static final int INDEX_DIRECTORY = 0;
    private static final int INDEX_FILE_NAME = 1;
    private static final int FILE_PATH_ARRAY_LENGTH = 2;
    
    /**
     * Moves file to the selected directory and updates Config accordingly
     * @param selectedDirectory directory to be changed to
     * @return true upon success operation, false otherwise
     */
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
    
    /**
     * Open selected file and updates Config accordingly. 
     * New taskmanager is loaded and model will be reset.
     * @param selectedFile file to read data from
     * @return true upon success operation, false otherwise
     */
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
    
    /**
     * Splits file path to directory and fileName
     * @param filePath path of file
     * @return String[FILE_PATH_ARRAY_LENGTH] containing directory and fileName
     */
    public static String[] splitFilePath(String filePath) {
        assert filePath != null;
        String[] separatedFilePath = new String[FILE_PATH_ARRAY_LENGTH];
        separatedFilePath[INDEX_DIRECTORY] = filePath.substring(0, filePath.lastIndexOf(File.separator));
        separatedFilePath[INDEX_FILE_NAME] = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        return separatedFilePath;
    }
    
    /**
     * Saves Config state by pushing it into stack
     * Config states only saved if it is storage command (openFile and changeDirectory)
     * @param isStorageOperation true if storage operation, false otherwise
     * @throws DataConversionException
     */
    public static void storeConfig(boolean isStorageOperation) throws DataConversionException {
        if (isStorageOperation) {
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            configHistory.push(config);
        } else {
            redoConfigHistory.clear();
            configHistory.push(null);
        }
    }
    
    /**
     * restoreConfig undo changes done to Config
     * If configHistory is empty or its top element is null, a mutating command is to be undo instead 
     * and method will push null element to redoConfigHistory
     * Else, perform corresponding commands to undo Config changes
     * @return true if undo config, false if undo mutating command
     * @throws DataConversionException
     */
    public static boolean restoreConfig() throws DataConversionException {
        if (configHistory.isEmpty()) {
            redoConfigHistory.push(null);
            return false;
        }
        Config originalConfig = configHistory.pop();
        Config currentConfig = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        redoConfigHistory.push(currentConfig);
        
        if (originalConfig == null) {
            redoConfigHistory.push(null);
            return false;
        } else if (originalConfig.getTaskManagerFileName().equals(currentConfig.getTaskManagerFileName())
                && !originalConfig.getTaskManagerFileDirectory().equals(currentConfig.getTaskManagerFileDirectory())) {
            updateDirectory(new File(originalConfig.getTaskManagerFileDirectory()));
        } else {
            updateFile(new File(originalConfig.getTaskManagerFilePath()));
        }
        return true;
    }
    
    /**
     * revertConfig redo changes done to Config
     * If redoConfigHistory is empty or its top element is null, a mutating command is to be redone instead 
     * and method will push null element to configHistory
     * Else, perform corresponding commands to redo Config changes
     * @return true if redo config, false if redo mutating command
     * @throws DataConversionException
     */
    public static boolean revertConfig() throws DataConversionException {
        if (redoConfigHistory.isEmpty()) {
            configHistory.push(null);
            return false;
        }
        
        Config redoConfig = redoConfigHistory.pop();
        Config currentConfig = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        configHistory.push(currentConfig);
        
        if (redoConfig == null) {
            configHistory.push(null);
            return false;
        } else if (redoConfig.getTaskManagerFileName().equals(currentConfig.getTaskManagerFileName())
                && !redoConfig.getTaskManagerFileDirectory().equals(currentConfig.getTaskManagerFileDirectory())) {
            updateDirectory(new File(redoConfig.getTaskManagerFileDirectory()));
        } else {
            updateFile(new File(redoConfig.getTaskManagerFilePath()));
        }
        return true;
    }
    
    //Returns true if configHistory is empty
    public static boolean isConfigHistoryEmpty() {
        return configHistory.isEmpty();
    }
    
    //Returns true if redoConfigHistory is empty
    public static boolean isRedoConfigHistoryEmpty() {
        return redoConfigHistory.isEmpty();
    }
}
