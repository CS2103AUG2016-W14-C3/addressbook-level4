package taskle.ui;

import java.io.File;
import java.util.Optional;

import org.controlsfx.control.NotificationPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import taskle.commons.core.Config;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.StorageUtil;

//@@author A0140047U
/**
 * 
 * Confirmation dialog to replace existing file
 *
 */
public class ExistingFileDialog {

    private static final String DIALOG_HEADER = "A Taskle data file currently exists in the specified folder.";
    private static final String DIALOG_CONTENT = "Replace existing file?";
    
    public static void load(NotificationPane notificationPane, Stage stage, File selectedDirectory) throws DataConversionException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(DIALOG_HEADER);
        alert.setContentText(DIALOG_CONTENT);
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            StorageUtil.storeConfig(true);
            StorageUtil.updateDirectory(selectedDirectory);
            Config config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
            
            notificationPane.show("Directory changed to: " + config.getTaskManagerFileDirectory());
        } 
    }
}
