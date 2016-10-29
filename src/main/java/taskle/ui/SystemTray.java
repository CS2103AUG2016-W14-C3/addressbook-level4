package taskle.ui;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import taskle.commons.core.LogsCenter;
import taskle.logic.Logic;
import taskle.model.task.Task;

// Java 8 code
public class SystemTray {

    private static final int NOTIFICATION_INTERVAL = 60 * 1000;

    private static final int NOTIFICATION_DELAY = 1 * 1000;
    
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);

    // one icon location is shared between the application tray icon and task
    // bar icon.
    // you could also use multiple icons to allow for clean display of tray
    // icons on hi-dpi devices.
    private final BufferedImage iconApplication;

    // application stage is stored so that it can be shown and hidden based on
    // system tray icon operations.
    private Stage stage;

    private final Logic logic;
    
    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer notificationTimer = new Timer();

    // sets up the javafx application.
    // a tray icon is setup for the icon, but the main stage remains invisible
    // until the user
    // interacts with the tray icon.
    // @Override public void start(final Stage stage) {
    // // stores a reference to the stage.
    // this.stage = stage;
    //
    // // instructs the javafx system not to exit implicitly when the last
    // application window is shut.
    // Platform.setImplicitExit(false);
    //
    // // sets up the tray icon (using awt code run on the swing thread).
    // javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
    //
    // // out stage will be translucent, so give it a transparent style.
    // stage.initStyle(StageStyle.TRANSPARENT);
    //
    // // create the layout for the javafx stage.
    // StackPane layout = new StackPane(createContent());
    // layout.setStyle(
    // "-fx-background-color: rgba(255, 255, 255, 0.5);"
    // );
    // layout.setPrefSize(300, 200);
    //
    // // this dummy app just hides itself when the app screen is clicked.
    // // a real app might have some interactive UI and a separate icon which
    // hides the app window.
    // layout.setOnMouseClicked(event -> stage.hide());
    //
    // // a scene with a transparent fill is necessary to implement the
    // translucent app window.
    // Scene scene = new Scene(layout);
    // scene.setFill(Color.TRANSPARENT);
    //
    // stage.setScene(scene);
    // }

    public SystemTray(Logic logic, javafx.scene.image.Image iconApplication, Stage stage) {
        this.iconApplication = SwingFXUtils.fromFXImage(iconApplication, null);
        this.stage = stage;
        this.logic = logic;
    }


    /**
     * Sets up a system tray icon for the application.
     */
    public void addAppToTray() {
        // ensure awt toolkit is initialized.
        java.awt.Toolkit.getDefaultToolkit();

        java.awt.SystemTray tray = setupTray();
        java.awt.TrayIcon trayIcon = setupTrayIcon();
        addMenuItems(tray, trayIcon);
        addNotificationTimer(trayIcon);
        // add the application tray icon to the system tray.
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }

    }
    
    /**
     *  Method to set up the tray in the task bar
     * @return
     */
    private java.awt.SystemTray setupTray() {
        // app requires system tray support, just exit if there is no support.
        if (!java.awt.SystemTray.isSupported()) {
            System.out.println("No system tray support, application exiting.");
            Platform.exit();
        }

        // set up a system tray.
        java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
        return tray;
    }
    
    /**
     *  Method to add the tray icon into the task bar
     * @return
     */
    private java.awt.TrayIcon setupTrayIcon() {
        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(iconApplication);
        trayIcon.setImageAutoSize(true);
        // if the user double-clicks on the tray icon, show the main app stage.
        trayIcon.addActionListener(event -> Platform.runLater(this::showStage));
        return trayIcon;
    }

    /**
     *  Method to add menu items into the tray icon and implement the
     *  required listeners for them.
     */
    private void addMenuItems(java.awt.SystemTray tray, java.awt.TrayIcon trayIcon) {
        // if the user selects the default menu item (which includes the app
        // name),
        // show the main app stage.
        java.awt.MenuItem openItem = new java.awt.MenuItem("Open Taskle");
        openItem.addActionListener(event -> Platform.runLater(this::showStage));

        // the convention for tray icons seems to be to set the default icon for
        // opening
        // the application stage in a bold font.
        java.awt.Font defaultFont = java.awt.Font.decode(null);
        java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
        openItem.setFont(boldFont);


        // to really exit the application, the user must go to the system tray
        // icon
        // and select the exit option, this will shutdown JavaFX and remove the
        // tray icon (removing the tray icon will also shut down AWT).
        java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
        exitItem.addActionListener(event -> {
            notificationTimer.cancel();
            Platform.exit();
            tray.remove(trayIcon);
        });

        // setup the popup menu for the application.
        final java.awt.PopupMenu popup = new java.awt.PopupMenu();
        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
    }
    
    /**
     *  Method to add a timer to notify the user of the reminders
     */
    private void addNotificationTimer(java.awt.TrayIcon trayIcon) {
        // create a timer which periodically displays a notification message.
        notificationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Date currentDate = new Date();
                List<Task> taskRemindDisplay = logic.verifyReminder(currentDate);
                if(taskRemindDisplay.isEmpty()) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < taskRemindDisplay.size(); i++) {
                    Task task = taskRemindDisplay.get(i);
                    sb.append(task.getName().fullName);
                    if(!task.getDetailsString().equals("")) {
                        sb.append(" Date: " + task.getDetailsString());
                    }
                    sb.append("\n");
                }
                
                javax.swing.SwingUtilities.invokeLater(() -> trayIcon.displayMessage("Reminder!",
                        sb.toString(), java.awt.TrayIcon.MessageType.INFO));
                
            }
        }, NOTIFICATION_DELAY, NOTIFICATION_INTERVAL);

    }
    
    private void dismissReminders() {
        
    }
    /**
     * Shows the application stage and ensures that it is brought ot the front
     * of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
}
