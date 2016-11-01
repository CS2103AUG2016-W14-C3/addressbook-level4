package taskle.ui;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.management.Notification;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import taskle.commons.core.LogsCenter;
import taskle.logic.Logic;
import taskle.model.task.Task;

/**
 * Code for system tray for the application.
 * Handles reminders notification
 * @author zhiyong
 * @@author A0139402M
 */
public class SystemTray {

    private static final int NOTIFICATION_INTERVAL = 60 * 1000;

    private static final int NOTIFICATION_DELAY = 1 * 1000;
    
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);

    private static java.awt.SystemTray tray;
    private static java.awt.TrayIcon trayIcon;
    private static Date currentDateTime;
    // one icon location is shared between the application tray icon and task
    // bar icon.
    // you could also use multiple icons to allow for clean display of tray
    // icons on hi-dpi devices.
    private final BufferedImage iconApplication;

    // application stage is stored so that it can be shown and hidden based on
    // system tray icon operations.
    private Stage stage;

    private static Logic logic;
    
    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer notificationTimer = new Timer();


    public SystemTray(Logic logic, javafx.scene.image.Image iconApplication, Stage stage) {
        this.iconApplication = SwingFXUtils.fromFXImage(iconApplication, null);
        this.stage = stage;
        SystemTray.logic = logic;
    }


    /**
     * Sets up a system tray icon for the application.
     */
    public void addAppToTray() {
        // ensure awt toolkit is initialized.
        java.awt.Toolkit.getDefaultToolkit();
        
        // app requires system tray support, just exit if there is no support.
        if (!java.awt.SystemTray.isSupported()) {
            String error = "No system tray support, application running without system tray.";
            logger.severe(error);
            Notifications.create()
                .title("Error")
                .text(error)
                .showWarning();
            return;
        }
        
        
        tray = setupTray();
        trayIcon = setupTrayIcon();
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

        java.awt.MenuItem dismissReminderItem = new java.awt.MenuItem("Dismiss Reminders");
        dismissReminderItem.addActionListener(event -> Platform.runLater(this::dismissReminders));
                
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
        popup.add(dismissReminderItem);
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
                currentDateTime = new Date();
                List<Task> taskRemindDisplay = logic.verifyReminder(currentDateTime);
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
    
    public static void showNotification() {
        currentDateTime = new Date();
        List<Task> taskRemindDisplay = logic.verifyReminder(currentDateTime);
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
    
    private void dismissReminders() {
        logic.dismissReminder(currentDateTime);
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
