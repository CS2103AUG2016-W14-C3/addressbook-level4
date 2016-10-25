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

    public void configure(
            NotificationPane notificationPane, 
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
    
    private void createPopOver() {
        popOver = new PopOver();
        VBox vBox = new VBox();
        popOverText = new Text();
        vBox.getChildren().add(popOverText);
        vBox.setPadding(new Insets(10));
        popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        popOver.setFadeInDuration(new Duration(300));
        popOver.setAutoHide(true);
        popOver.setContentNode(vBox);
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


    @FXML
    private void handleCommandInputChanged() {
        //Take a copy of the command text
        previousCommandText = commandTextField.getText();
        
        /* We assume the command is correct. If it is incorrect, the command box will be changed accordingly
         * in the event handling code {@link #handleIncorrectCommandAttempted}
         */
        mostRecentResult = logic.execute(previousCommandText);
        displayCommandFeedback(mostRecentResult);
        logger.info("Result: " + mostRecentResult.getFeedback());
    }
    
    private void displayCommandFeedback(CommandResult commandResult) {
        assert commandResult != null;
        
        String feedback = commandResult.getFeedback();
        if (!commandResult.wasValid()) {
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

    private void showIncorrectCommand(String feedback) {
        popOverText.setText(feedback);
        popOver.show(commandTextField);

    }
    
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
