package taskle.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import taskle.commons.core.LogsCenter;
import taskle.commons.events.model.TaskFilterChangedEvent;
import taskle.commons.util.FxViewUtil;

/**
 * A ui for the status bar that is displayed at the footer of the application.
 */
public class StatusDisplayPanel extends UiPart {
    private static final Logger logger = LogsCenter.getLogger(StatusDisplayPanel.class);

    private HBox mainPane;

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
    
    private void setupInitialChips() {
        ObservableList<Node> nodes = mainPane.getChildren();
        nodes.clear();
        nodes.add(pendingChip);
        nodes.add(overdueChip);
    }

    @Subscribe
    private void handleTaskFilterChangedEvent(TaskFilterChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        ObservableList<Node> nodes = mainPane.getChildren();
        nodes.clear();
        if (event.showAll) {
            nodes.add(allChip);
            return;
        }
        
        if (event.showPending) {
            nodes.add(pendingChip);
        }
        
        if (event.showOverdue) {
            nodes.add(overdueChip);
        }
        
        if (event.showDone) {
            nodes.add(doneChip);
        }
        
    }
    
}
