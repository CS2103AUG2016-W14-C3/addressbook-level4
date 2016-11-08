package taskle.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import taskle.model.task.ReadOnlyTask;
//@@author A0141780J
public class TaskCard extends UiPart {

    private static final String STYLE_CARD_BG_DEFAULT = "-fx-background-color: #546E7A";
    private static final String STYLE_CARD_BG_DONE = "-fx-background-color: #455A64";
    
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
    @FXML
    private Rectangle colorTag;
    
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
        cardPane.setStyle(STYLE_CARD_BG_DEFAULT);
        
        switch (task.getStatus()) {
        case DONE:
            colorTag.setFill(Color.MEDIUMSEAGREEN);
            cardPane.setStyle(STYLE_CARD_BG_DONE);
            break;
        case OVERDUE:
            colorTag.setFill(Color.ORANGERED);
            break;
        default:
            colorTag.setFill(Color.DODGERBLUE);
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
