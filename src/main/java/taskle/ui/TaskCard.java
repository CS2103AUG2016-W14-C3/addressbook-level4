package taskle.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import taskle.model.task.ReadOnlyTask;
//@author A0141780J
public class TaskCard extends UiPart {

    private static final String STYLE_CARD_DONE = "-fx-background-color: #546E7A";
    private static final String STYLE_CARD_OVERDUE = "-fx-background-color: #E53935";
    private static final String STYLE_CARD_PENDING = "-fx-background-color: #1976D2";
    private static final String STYLE_CARD_FLOAT = "-fx-background-color: #009688";
    
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
    private Label reminderDate;
    
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
        reminderDate.setText(task.getRemindDetailsString());
        id.setText(displayedIndex + ". ");
    }
    
    private void setCardStyle() {
        switch (task.getStatus()) {
        case DONE:
            cardPane.setStyle(STYLE_CARD_DONE);
            break;
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
            cardPane.setStyle(STYLE_CARD_FLOAT);
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
