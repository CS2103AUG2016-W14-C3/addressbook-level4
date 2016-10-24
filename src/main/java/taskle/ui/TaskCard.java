package taskle.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import taskle.model.task.ReadOnlyTask;

public class TaskCard extends UiPart {

    private static final String STYLE_CARD_OVERDUE = "-fx-background-color: #E53935";
    private static final String STYLE_CARD_PENDING = "-fx-background-color: #1976D2";
    private static final String STYLE_CARD_FLOAT = "-fx-background-color: #1976D2";
    
    private static final String FXML = "TaskListCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label details;
    @FXML
    private Label id;
    @FXML
    private Label tags;

    private ReadOnlyTask task;
    private int displayedIndex;

    public TaskCard(){

    }

    public static TaskCard load(ReadOnlyTask task, int displayedIndex){
        TaskCard card = new TaskCard();
        card.task = task;
        card.displayedIndex = displayedIndex;
        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        setCardTexts();
        setCardStyle();
    }
    
    private void setCardTexts() {
        name.setText(task.getName().fullName);
        details.setText(task.getDetailsString());
        id.setText(displayedIndex + ". ");
        tags.setText(task.tagsString());
    }
    
    private void setCardStyle() {
        switch (task.getState()) {
        case FLOAT:
            cardPane.setStyle(STYLE_CARD_FLOAT);
            break;
        case OVERDUE:
            cardPane.setStyle(STYLE_CARD_OVERDUE);
            break;
        case PENDING:
            cardPane.setStyle(STYLE_CARD_PENDING);
            break;
        default:
            break;
        }
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
