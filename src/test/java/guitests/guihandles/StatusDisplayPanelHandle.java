package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;
import taskle.TestApp;
//@@author A0141780J

/**
 * A handler for the PopOver of the UI
 */
public class StatusDisplayPanelHandle extends GuiHandle {

    private static final String PENDING_CHIP_ID = "#pendingChip";
    private static final String OVERDUE_CHIP_ID = "#overdueChip";
    private static final String DONE_CHIP_ID = "#doneChip";
    private static final String ALL_CHIP_ID = "#allChip";

    public StatusDisplayPanelHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }
    
    public boolean isAllShown() {
        return containsNode(ALL_CHIP_ID);
    }
    
    public boolean isDoneShown() {
        return containsNode(DONE_CHIP_ID);
    }
    
    public boolean isPendingShown() {
        return containsNode(PENDING_CHIP_ID);
    }
    
    public boolean isOverdueShown() {
        return containsNode(OVERDUE_CHIP_ID);
    }
}
