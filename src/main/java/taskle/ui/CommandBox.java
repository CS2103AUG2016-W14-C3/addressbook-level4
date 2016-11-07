package taskle.ui;

import java.util.logging.Logger;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import taskle.commons.core.LogsCenter;
import taskle.commons.events.ui.IncorrectCommandAttemptedEvent;
import taskle.commons.util.FxViewUtil;
import taskle.logic.Logic;
import taskle.logic.commands.CommandResult;

public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";
    private static final String POP_OVER_TEXT_ID = "popOverText";
    private static final String POP_OVER_ID = "popOver";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private NotificationPane notificationPane;
    private PopOver popOver;
    private Text popOverText;
    String previousCommandText;

    private Logic logic;

    @FXML
    private TextField commandTextField;
    private CommandResult mostRecentResult;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder,
            NotificationPane notificationPane, Logic logic) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(notificationPane, logic);
        commandBox.addToPlaceholder();
        return commandBox;
    }

    //@@author A0141780J
    public void configure(NotificationPane notificationPane, 
                          Logic logic) {
        this.notificationPane = notificationPane;
        this.logic = logic;
        createPopOver();
        registerAsAnEventHandler(this);
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(commandTextField);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
    }
    
    /** Creates a pop over with content layout and style specified. */
    private void createPopOver() {
        popOver = new PopOver();
        popOver.setId(POP_OVER_ID);
        setPopOverLayout();
        setPopOverStyle();
    }
    
    /** Sets up the layout inside popover. */
    private void setPopOverLayout() {
        popOverText = new Text();
        popOverText.setId(POP_OVER_TEXT_ID);
        
        VBox vBox = new VBox();
        vBox.getChildren().add(popOverText);
        vBox.setPadding(new Insets(10));
        popOver.setContentNode(vBox);
    }
    
    /** Sets up the style for popover and how it is displayed. */
    private void setPopOverStyle() {
        popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        popOver.setFadeInDuration(new Duration(300));
        popOver.setDetachable(false);
        popOver.setAutoHide(true);
    }

    @Override
    public void setNode(Node node) {
        commandPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    /**
     * Java FXML method that is called everytime there's a 
     * new command input.
     */
    @FXML
    private void handleCommandInputChanged() {
        //Take a copy of the command text
        previousCommandText = commandTextField.getText();
        
        // execute command and display command feedback
        mostRecentResult = logic.execute(previousCommandText);
        displayCommandFeedback(mostRecentResult);
        logger.info("Result: " + mostRecentResult.getFeedback());
    }
    
    /**
     * Displays command feedback based on results.
     * If result is not successful, no feedback is displayed.
     * If successful, feedback is displayed.
     * 
     * @param commandResult Command result to use for display.
     */
    private void displayCommandFeedback(CommandResult commandResult) {
        assert commandResult != null;
        
        String feedback = commandResult.getFeedback();
        if (!commandResult.isSuccessful()) {
            return;
        }
        
        showCorrectCommand(feedback);
    }

    @Subscribe
    private void handleIncorrectCommandAttempted(IncorrectCommandAttemptedEvent event){
        logger.info(LogsCenter.getEventHandlingLogMessage(event,"Invalid command: " + previousCommandText));
        showIncorrectCommand(event.getFeedback());
        restoreCommandText();
    }

    /**
     * Shows the UI elements for incorrect command.
     * 
     * @param feedback feedback message to user to incorrect command.
     */
    private void showIncorrectCommand(String feedback) {
        popOverText.setText(feedback);
        popOver.show(commandTextField);
        notificationPane.hide();
    }
    
    /**
     * Shows the UI elements for correct command.
     * 
     * @param feedback feedback message to user for correct command.
     */
    private void showCorrectCommand(String feedback) {
        popOver.hide();
        commandTextField.clear();
        notificationPane.show(feedback);
    }
    
    /**
     * Restores the command box text to the previously entered command
     */
    private void restoreCommandText() {
        commandTextField.setText(previousCommandText);
        commandTextField.positionCaret(previousCommandText.length());
    }

}
