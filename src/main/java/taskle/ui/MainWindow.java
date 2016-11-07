package taskle.ui;

import java.awt.Point;
import java.io.File;

import org.controlsfx.control.NotificationPane;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import taskle.commons.core.Config;
import taskle.commons.core.EventsCenter;
import taskle.commons.core.GuiSettings;
import taskle.commons.events.storage.StorageMenuItemRequestEvent;
import taskle.commons.events.ui.ExitAppRequestEvent;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.ConfigUtil;
import taskle.commons.util.StorageUtil;
import taskle.logic.Logic;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.OpenFileCommand;
import taskle.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart {

    private static final String NOTIFICATION_PANE_ID = "notificationPane";
    private static final String GOOGLE_CUSTOM_FONT_URL = "https://fonts.googleapis.com/css?family=Roboto";
    private static final String ICON = "/images/ic_task_manager.png";
    private static final String FXML = "MainWindow.fxml";
    public static final int MIN_HEIGHT = 600;
    public static final int MIN_WIDTH = 450;

    private static final String FILE_CHOOSER_NAME = "Taskle Data Files";
    private static final String FILE_CHOOSER_TYPE = "*.xml";
    private static final String CHANGE_FILE_SUCCESS = "Storage File has been changed.";
    private static final String CHANGE_FILE_ERROR = "Invalid file format detected. Unable to open file.";
    private static final String CHANGE_DIRECTORY_SUCCESS = "Storage Directory has been changed to %1$s";
    private static final String CHANGE_DIRECTORY_FAILURE = "An error occurred when changing directory.";

    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private TaskListPanel taskListPanel;
    private StatusBarFooter statusBarFooter;
    private StatusDisplayPanel statusDisplayPanel;
    private CommandBox commandBox;
    private Config config;
    private UserPrefs userPrefs;

    // Handles to elements of this Ui container
    private NotificationPane notificationPane;
    private VBox rootLayout;
    private Scene scene;
    
    @FXML
    private AnchorPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private AnchorPane taskListPanelPlaceholder;

    @FXML
    private AnchorPane statusbarPlaceholder;
    
    @FXML
    private AnchorPane statusDisplayPanelPlaceholder;
    
    public MainWindow() {
        super();
    }

    @Override
    public void setNode(Node node) {
        rootLayout = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    public static MainWindow load(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {

        MainWindow mainWindow = UiPartLoader.loadUiPart(primaryStage, new MainWindow());
        mainWindow.configure(config.getAppTitle(), config.getTaskManagerName(), config, prefs, logic);
        return mainWindow;
    }

    //@author A0141780J
    private void configure(String appTitle, String taskManagerName, Config config, UserPrefs prefs,
                           Logic logic) {

        //Set dependencies
        this.logic = logic;
        this.config = config;
        this.userPrefs = prefs;

        //Configure the UI
        setTitle(appTitle);
        setIcon(ICON);
        setWindowMinSize();
        setWindowDefaultSize(prefs);
        
        setupNotificationPane();
        scene = new Scene(notificationPane);
        primaryStage.setScene(scene);

        setCustomFont();
        setAccelerators();
    }
    
    private void setCustomFont() {
        scene.getStylesheets().add(GOOGLE_CUSTOM_FONT_URL);
    }

    private void setAccelerators() {
        helpMenuItem.setAccelerator(KeyCombination.valueOf("F1"));
    }

    void fillInnerParts() {
        taskListPanel = TaskListPanel.load(primaryStage, getTaskListPlaceholder(), logic.getFilteredTaskList());
        statusBarFooter = StatusBarFooter.load(primaryStage, getStatusbarPlaceholder(), config.getTaskManagerFilePath());
        statusDisplayPanel = StatusDisplayPanel.load(primaryStage, getStatusDisplayPanelPlaceholder());
        commandBox = CommandBox.load(primaryStage, getCommandBoxPlaceholder(), notificationPane, logic);
    }

    private AnchorPane getCommandBoxPlaceholder() {
        return commandBoxPlaceholder;
    }

    private AnchorPane getStatusbarPlaceholder() {
        return statusbarPlaceholder;
    }
    
    private AnchorPane getStatusDisplayPanelPlaceholder() {
        return statusDisplayPanelPlaceholder;
    }

    public AnchorPane getTaskListPlaceholder() {
        return taskListPanelPlaceholder;
    }

    public void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the default size based on user preferences.
     */
    protected void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        Point windowCoords = prefs.getGuiSettings().getWindowCoordinates();
        if (windowCoords != null && windowCoords.getX() > 0 && windowCoords.getY() > 0) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    private void setWindowMinSize() {
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    public GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = HelpWindow.load(primaryStage);
        helpWindow.show();
    }
    

    public void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    //@@author A0140047U
    /**
     * Change storage file location.
     * 
     * @throws DataConversionException 
     */
    @FXML
    private void handleChangeDirectory() throws DataConversionException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        
        if (selectedDirectory == null) {
        } else if ((selectedDirectory.getAbsolutePath()).equals(config.getTaskManagerFileDirectory())) {
        } else if (new File(selectedDirectory.getAbsolutePath(), config.getTaskManagerFileName()).exists()) {
            ExistingFileDialog.load(notificationPane, primaryStage, selectedDirectory);
        } else {
            EventsCenter.getInstance().post(new StorageMenuItemRequestEvent(ChangeDirectoryCommand.COMMAND_WORD, true));
            if (StorageUtil.updateDirectory(selectedDirectory)) {
                config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
                notificationPane.show(String.format(CHANGE_DIRECTORY_SUCCESS, config.getTaskManagerFileDirectory()));
            } else {
                EventsCenter.getInstance().post(new StorageMenuItemRequestEvent(ChangeDirectoryCommand.COMMAND_WORD, false));
                notificationPane.show(CHANGE_DIRECTORY_FAILURE);
            }
        }
    }
    
    /**
     * Change storage file.
     * 
     * @throws DataConversionException 
     */
    @FXML
    private void handleChangeStorageFile() throws DataConversionException {
        config = ConfigUtil.readConfig(Config.DEFAULT_CONFIG_FILE).get();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter(FILE_CHOOSER_NAME, FILE_CHOOSER_TYPE));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null && !selectedFile.getAbsolutePath().equals(config.getTaskManagerFilePath())) {
            EventsCenter.getInstance().post(new StorageMenuItemRequestEvent(OpenFileCommand.COMMAND_WORD, true));
            if (StorageUtil.updateFile(selectedFile)) {
                notificationPane.show(CHANGE_FILE_SUCCESS);
            } else {
                notificationPane.show(CHANGE_FILE_ERROR);
                EventsCenter.getInstance().post(new StorageMenuItemRequestEvent(OpenFileCommand.COMMAND_WORD, false));
            }
        }
    }
    
    private void setupNotificationPane() {
        notificationPane = new NotificationPane(rootLayout);
        notificationPane.setId(NOTIFICATION_PANE_ID);
    }

    //@@author
    public TaskListPanel getTaskListPanel() {
        return this.taskListPanel;
    }

}
