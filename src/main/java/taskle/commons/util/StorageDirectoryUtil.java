package taskle.commons.util;

import java.io.File;
import java.io.IOException;

import taskle.commons.core.Config;
import taskle.commons.core.EventsCenter;
import taskle.commons.events.storage.StorageChangeRequestEvent;
import taskle.commons.exceptions.DataConversionException;
import taskle.model.ReadOnlyTaskManager;
import taskle.storage.XmlFileStorage;

//@@author A0140047U
/**
 * 
 * Manage changes in directory of storage file
 *
 */
public class StorageDirectoryUtil {

    public static void updateDirectory(File selectedDirectory) {
        assert selectedDirectory != null;
        try {
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()).delete();
            new File(config.getTaskManagerFilePath()).renameTo(new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()));
            config.setTaskManagerFileDirectory(selectedDirectory.getAbsolutePath());
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
            EventsCenter.getInstance().post(new StorageChangeRequestEvent(config.getTaskManagerFilePath(),null));
        } catch (IOException | DataConversionException e) {
            e.printStackTrace();
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
}
