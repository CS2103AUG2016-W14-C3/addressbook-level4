package taskle.ui;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import taskle.commons.core.LogsCenter;
import taskle.model.help.CommandGuide;
//@author A0141780J
/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart {

    private static final String SPACE_STRING = " ";
    private static final String COLUMN_NAME_FORMAT = "Command Format";
    private static final String COLUMN_NAME_ACTION = "Action";
    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "HelpWindow.fxml";
    private static final String TITLE = "Help";
    private static final List<CommandGuide> LIST_COMMAND_GUIDES = new ArrayList<>(
            Arrays.asList(new CommandGuide("Addition of Tasks", "add", "task_name", "[remind date time]"),
                    new CommandGuide("", "add", "deadline_name", "by", 
                            "[date time]", "[remind date time]"),
                    new CommandGuide("", "add", "event_name", "from", 
                            "[date time]", "to", "[date time]", "[remind date time]"),
                    new CommandGuide("Editing of Tasks", "edit", "task_number", "new_task_name"),
                    new CommandGuide("", "reschedule", "task_number", "to", "[date time]", "[remind date time]"),
                    new CommandGuide("", "reschedule", "task_number", "clear"),
                    new CommandGuide("", "remind", "task_number", "[date time]"),
                    new CommandGuide("", "remind", "task_number", "clear"),
                    new CommandGuide("Removal of Tasks", "remove", "task_number"), 
                    new CommandGuide("Undo Previous Command", "undo"),
                    new CommandGuide("Redo Previous Command", "redo"),
                    new CommandGuide("Finding of Tasks", "find", "keywords", "[-status]"),
                    new CommandGuide("Listing of Tasks", "list", "[-status]"),
                    new CommandGuide("Marking Tasks as Done", "done", "task_number"),
                    new CommandGuide("Clearing of Tasks", "clear"),
                    new CommandGuide("Changing of Save Directory", "save", "directory_path"),
                    new CommandGuide("Opening of File", "open", "file_path"),
                    new CommandGuide("Help Window Display", "help"),
                    new CommandGuide("Exiting from Taskle", "exit")));

    private AnchorPane mainPane;
    private Stage dialogStage;

    @FXML
    private TableView<CommandGuide> helpTable;

    public static HelpWindow load(Stage primaryStage) {
        logger.fine("Showing help page about the application.");
        HelpWindow helpWindow = UiPartLoader.loadUiPart(primaryStage, new HelpWindow());
        helpWindow.configure();
        return helpWindow;
    }

    @Override
    public void setNode(Node node) {
        mainPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    private void configure() {
        Scene scene = new Scene(mainPane);
        // Null passed as the parent stage to make it non-modal.
        dialogStage = createDialogStage(TITLE, null, scene);
        dialogStage.setMaximized(false);
        setIcon(dialogStage, ICON);
        setupHelpTable();
    }

    private void setupHelpTable() {
        ObservableList<CommandGuide> observableGuides = 
                FXCollections.observableArrayList(LIST_COMMAND_GUIDES);
        helpTable.setItems(observableGuides);
        setupHelpColumns();
    }

    private void setupHelpColumns() {
        setupNameCol();
        setupFormatCol();
    }

    private void setupNameCol() {
        TableColumn<CommandGuide, String> nameCol = 
                new TableColumn<>(COLUMN_NAME_ACTION);
        bindNameColString(nameCol);
        setNameColStyle(nameCol);
        nameCol.setSortable(false);
        helpTable.getColumns().add(nameCol);
    }

    private void setupFormatCol() {
        TableColumn<CommandGuide, String> formatCol = 
                new TableColumn<>(COLUMN_NAME_FORMAT);
        bindFormatColString(formatCol);
        setFormatColStyle(formatCol);
        formatCol.setSortable(false);
        helpTable.getColumns().add(formatCol);
    }

    /**
     * This method binds the Strings for name columns to the CommandGuide 
     * objects.
     * @param nameCol Name Column for help window
     */
    private void bindNameColString(TableColumn<CommandGuide, String> nameCol) {
        nameCol.setCellValueFactory(
                new Callback<CellDataFeatures<CommandGuide, String>, 
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<CommandGuide, String> param) {
                String actionName = param.getValue().getName();
                return getNameColString(actionName);
            }
        });
    }

    /**
     * This method binds the Strings for format columns to the CommandGuide
     * objects.
     * @param formatCol Format Column for help window
     */
    private void bindFormatColString(TableColumn<CommandGuide, String> formatCol) {
        formatCol.setCellValueFactory(
                new Callback<CellDataFeatures<CommandGuide, String>, 
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<CommandGuide, String> param) {
                CommandGuide commandGuide = param.getValue();
                String commandWord = commandGuide.getCommandWord();
                String[] commandArgs = commandGuide.getArgs();
                return getFormatColString(commandWord, commandArgs);
            }
        });
    }

    private void setNameColStyle(TableColumn<CommandGuide, String> nameCol) {
        nameCol.prefWidthProperty().bind(helpTable.widthProperty().multiply(0.3));
        nameCol.setResizable(false);
    }

    private void setFormatColStyle(TableColumn<CommandGuide, String> formatCol) {
        formatCol.prefWidthProperty().bind(helpTable.widthProperty().multiply(0.7));
        formatCol.setResizable(false);
        formatCol.setCellFactory(
                new Callback<TableColumn<CommandGuide, String>, 
                TableCell<CommandGuide, String>>() {
            @Override
            public HelpTableCell call(TableColumn<CommandGuide, String> param) {
                return new HelpTableCell();
            }});
    }

    private ObservableValue<String> getNameColString(String name) {
        return new SimpleStringProperty(name);
    }

    private ObservableValue<String> getFormatColString(String commandWord, String... args) {
        String argsStrings = String.join(SPACE_STRING, args);
        return new SimpleStringProperty(String.join(SPACE_STRING, commandWord, argsStrings));
    }

    public void show() {
        dialogStage.showAndWait();
    }
}