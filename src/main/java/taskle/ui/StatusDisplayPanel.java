package taskle.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import taskle.commons.core.LogsCenter;
import taskle.commons.events.model.TaskFilterChangedEvent;
import taskle.commons.util.FxViewUtil;

//@@author A0141780J
/**
 * A ui for the status display panel that is displayed on top of the command box
 * in the application.
 */
public class StatusDisplayPanel extends UiPart {
    private static final Logger logger = LogsCenter.getLogger(StatusDisplayPanel.class);

    private HBox mainPane;

    @FXML
    private Label displayLabel;
        
    @FXML
    private ImageView allChip;
    
    @FXML
    private ImageView pendingChip;

    @FXML
    private ImageView overdueChip;
    
    @FXML
    private ImageView doneChip;

    private AnchorPane placeHolder;

    private static final String FXML = "StatusDisplayPanel.fxml";

    public static StatusDisplayPanel load(Stage stage, AnchorPane placeHolder) {
        StatusDisplayPanel statusDisplayPanel = UiPartLoader.loadUiPart(stage, placeHolder, new StatusDisplayPanel());
        statusDisplayPanel.configure();
        return statusDisplayPanel;
    }

    public void configure() {
        addMainPane();
        setupInitialChips();
        registerAsAnEventHandler(this);
    }

    private void addMainPane() {
        FxViewUtil.applyAnchorBoundaryParameters(mainPane, 0.0, 0.0, 0.0, 0.0);
        placeHolder.getChildren().add(mainPane);
    }

    @Override
    public void setNode(Node node) {
        mainPane = (HBox) node;
    }

    @Override
    public void setPlaceholder(AnchorPane placeholder) {
        this.placeHolder = placeholder;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
    
    /**
     * Prepares the initial chips, namely the pending and overdue chips and
     * the display label.
     */
    private void setupInitialChips() {
        ObservableList<Node> nodes = mainPane.getChildren();
        nodes.clear();
        nodes.add(displayLabel);
        nodes.add(pendingChip);
        nodes.add(overdueChip);
    }

    @Subscribe
    private void handleTaskFilterChangedEvent(TaskFilterChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        ObservableList<Node> nodes = mainPane.getChildren();
        nodes.clear();
        nodes.add(displayLabel);
        updateNodes(nodes, event);
    }
    
    /**
     * Updates the nodes to reflect the TaskFilterChangedEvent.
     * 
     * @param nodes List of nodes used.
     * @param event Event that reflects the new filters.
     */
    private void updateNodes(
            ObservableList<Node> nodes, TaskFilterChangedEvent event) {
        if (event.isAllShown) {
            nodes.add(allChip);
            return;
        }
        
        updatePendingNode(nodes, event);
        updateDoneNode(nodes, event);
        updateOverdueNode(nodes, event);
    }
    
    private void updatePendingNode(ObservableList<Node> nodes, 
                                   TaskFilterChangedEvent event) {
        if (event.isPendingShown) {
            nodes.add(pendingChip);
        }
    }
    
    private void updateDoneNode(ObservableList<Node> nodes, 
                                TaskFilterChangedEvent event) {
        if (event.isDoneShown) {
            nodes.add(doneChip);
        } 
    }
    
    private void updateOverdueNode(ObservableList<Node> nodes, 
                                   TaskFilterChangedEvent event) {
        if (event.isOverdueShown) {
            nodes.add(overdueChip);
        }
    }
    
    
    
}
