package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.Node;
import javafx.stage.Stage;
import taskle.model.task.ReadOnlyTask;

/**
 * Provides a handle to a person card in the person list panel.
 */
public class TaskCardHandle extends GuiHandle {
    private static final String NAME_FIELD_ID = "#name";
    private static final String DETAILS_FIELD_ID = "#details";

    private Node node;

    public TaskCardHandle(GuiRobot guiRobot, Stage primaryStage, Node node){
        super(guiRobot, primaryStage, null);
        this.node = node;
    }

    protected String getTextFromLabel(String fieldId) {
        return getTextFromLabel(fieldId, node);
    }

    public String getFullName() {
        return getTextFromLabel(NAME_FIELD_ID);
    }

    public String getDetails() {
        return getTextFromLabel(DETAILS_FIELD_ID);
    }

    public boolean isSameTask(ReadOnlyTask task){
        System.out.println(getDetails() + " vs " + task.getDetailsString());
        return getFullName().equals(task.getName().fullName)
                && getDetails().equals(task.getDetailsString());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TaskCardHandle) {
            TaskCardHandle handle = (TaskCardHandle) obj;
            return getFullName().equals(handle.getFullName())
                    && getDetails().equals(handle.getDetails());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getFullName() + " " + getDetails();
    }
}
