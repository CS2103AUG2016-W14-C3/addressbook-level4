package taskle.commons.util;

import java.io.File;
import java.io.IOException;

import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.logic.Logic;
import taskle.model.ReadOnlyTaskManager;
import taskle.storage.XmlFileStorage;

/**
 * 
 * Manage changes in directory of storage file
 *
 */
public class StorageDirectoryUtil {

    public static void updateDirectory(Config config, Logic logic, File selectedDirectory) {
        assert selectedDirectory != null;
        try {
            new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()).delete();
            new File(config.getTaskManagerFilePath()).renameTo(new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()));
            config.setTaskManagerFileDirectory(selectedDirectory.getAbsolutePath());
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
            logic.changeDirectory(config.getTaskManagerFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean updateFile(Config config, Logic logic, File selectedFile) {
        assert selectedFile != null;
        ReadOnlyTaskManager newTaskManager;
        try {
            newTaskManager = XmlFileStorage.loadDataFromSaveFile(selectedFile);
            config.setTaskManagerFileDirectory(splitFilePath(selectedFile.getAbsolutePath())[0]);
            config.setTaskManagerFileName(splitFilePath(selectedFile.getAbsolutePath())[1]);
            ConfigUtil.saveConfig(config, Config.DEFAULT_CONFIG_FILE);
            logic.changeDirectory(config.getTaskManagerFilePath());
            logic.resetModel(newTaskManager);
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
