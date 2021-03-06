package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;
import taskle.TestApp;

/**
 * Provides a handle for the main GUI.
 */
public class MainGuiHandle extends GuiHandle {

    public MainGuiHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public TaskListPanelHandle getTaskListPanel() {
        return new TaskListPanelHandle(guiRobot, primaryStage);
    }

    public CommandBoxHandle getCommandBox() {
        return new CommandBoxHandle(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public MainMenuHandle getMainMenu() {
        return new MainMenuHandle(guiRobot, primaryStage);
    }
    
    public NotificationPaneHandle getNotificationPane() {
        return new NotificationPaneHandle(guiRobot, primaryStage);
    }
    
    public PopOverHandle getPopOver() {
        return new PopOverHandle(guiRobot, primaryStage);
    }
    
    public StatusDisplayPanelHandle getStatusDisplayPanel() {
        return new StatusDisplayPanelHandle(guiRobot, primaryStage);
    }

}
