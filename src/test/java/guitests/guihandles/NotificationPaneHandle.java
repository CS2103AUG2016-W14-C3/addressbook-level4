package guitests.guihandles;

import org.controlsfx.control.NotificationPane;
import org.testfx.service.query.NodeQuery;

import guitests.GuiRobot;
import javafx.stage.Stage;
import taskle.TestApp;

//@author A0141780J
/**
 * Provides a handle for the notification pane.
 */
public class NotificationPaneHandle extends GuiHandle {
    private static final String NOTIFICATION_PANE_ID = "#notificationPane";

    public NotificationPaneHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }
    
    public String getText() {
        return getNotificationPane().getText();
    }
    
    public boolean isShowing() {
        return getNotificationPane().isShowing();
    }

    private NotificationPane getNotificationPane() {
        return ((NotificationPane) getNode(NOTIFICATION_PANE_ID));
    }

}
