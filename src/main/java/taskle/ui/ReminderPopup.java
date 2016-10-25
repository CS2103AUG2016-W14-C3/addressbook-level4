package taskle.ui;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import taskle.commons.core.LogsCenter;

public class ReminderPopup extends UiPart{
    private static final String TITLE = "Help";
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "ReminderPopup.fxml";
    private static final Logger logger = LogsCenter.getLogger(ReminderPopup.class);
    private Stage dialogStage;
    private AnchorPane mainPane;

    public static ReminderPopup load(Stage primaryStage) {
        logger.fine("Showing reminder popup.");
        ReminderPopup reminderPopup = UiPartLoader.loadUiPart(primaryStage, new ReminderPopup());
        reminderPopup.configure();
        return reminderPopup;
    }
    
    private void configure() {
        Scene scene = new Scene(mainPane);
        // Null passed as the parent stage to make it non-modal.
        dialogStage = createDialogStage(TITLE, null, scene);
        dialogStage.setMaximized(false);
        setIcon(dialogStage, ICON);
    }
    
    public void show() {
        dialogStage.showAndWait();
    }
    
    @Override
    public void setNode(Node node) {
        mainPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

}
