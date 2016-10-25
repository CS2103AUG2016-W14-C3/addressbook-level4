package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import taskle.TestApp;
//@author A0141780J

/**
 * A handler for the PopOver of the UI
 */
public class PopOverHandle extends GuiHandle {

    public static final String POP_OVER_TEXT_ID = "#popOverText";
    private static final String POP_OVER_ID = "#popOver";

    public PopOverHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public String getText() {
        Text popupText = getPopOverText();
        return popupText.getText();
    }
    
    public boolean isShowing() {
        return true;
    }
    
    private Text getPopOverText() {
        Text result = (Text)getNode(POP_OVER_TEXT_ID);
        return result;
    }
}
