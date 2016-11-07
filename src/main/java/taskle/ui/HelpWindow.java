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
import taskle.logic.commands.AddCommand;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.ClearCommand;
import taskle.logic.commands.DoneCommand;
import taskle.logic.commands.EditCommand;
import taskle.logic.commands.ExitCommand;
import taskle.logic.commands.FindCommand;
import taskle.logic.commands.HelpCommand;
import taskle.logic.commands.ListCommand;
import taskle.logic.commands.OpenFileCommand;
import taskle.logic.commands.RedoCommand;
import taskle.logic.commands.RemindCommand;
import taskle.logic.commands.RemoveCommand;
import taskle.logic.commands.RescheduleCommand;
import taskle.logic.commands.UndoCommand;
import taskle.model.help.CommandGuide;
//@author A0141780J
/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart {

    private static final String SPACE_STRING = " ";
    private static final String COLUMN_NAME_FORMAT = "Command Format";
    private static final String COLUMN_NAME_ACTION = "Action";
    private static final String COLUMN_NAME_SHORTCUT = "Shortcut";
    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String ICON = "/images/help_icon.png";
    private static final String FXML = "HelpWindow.fxml";
    private static final String TITLE = "Help";
    
    // List of command guides to be used for help page
    private static final List<CommandGuide> LIST_COMMAND_GUIDES = new ArrayList<>(
            Arrays.asList(new CommandGuide("Addition of Tasks", AddCommand.COMMAND_WORD_SHORT, AddCommand.COMMAND_WORD,
                            "task_name", "[remind date time]"),
                    new CommandGuide("", "", AddCommand.COMMAND_WORD, "task_name", "by", 
                            "[date time]", "[remind date time]"),
                    new CommandGuide("", "", AddCommand.COMMAND_WORD, "task_name", "from", 
                            "[date time]", "to", "[date time]", "[remind date time]"),
                    new CommandGuide("Editing of Tasks", EditCommand.COMMAND_WORD_SHORT, EditCommand.COMMAND_WORD, 
                            "task_number", "new_task_name"),
                    new CommandGuide("", RescheduleCommand.COMMAND_WORD_SHORT, RescheduleCommand.COMMAND_WORD,
                            "task_number", "date [time]", "[to date time]", "[remind date time]"),
                    new CommandGuide("", "", RescheduleCommand.COMMAND_WORD, 
                            "task_number", "clear"),
                    new CommandGuide("", RemindCommand.COMMAND_WORD_SHORT, RemindCommand.COMMAND_WORD, 
                            "task_number", "date [time]"),
                    new CommandGuide("", "" , RemindCommand.COMMAND_WORD,
                            "task_number", "clear"),
                    new CommandGuide("Removal of Tasks", RemoveCommand.COMMAND_WORD_SHORT, RemoveCommand.COMMAND_WORD,
                            "task_number"), 
                    new CommandGuide("Undo Previous Command", UndoCommand.COMMAND_WORD_SHORT, UndoCommand.COMMAND_WORD),
                    new CommandGuide("Redo Previous Command", RedoCommand.COMMAND_WORD_SHORT, RedoCommand.COMMAND_WORD),
                    new CommandGuide("Finding of Tasks", FindCommand.COMMAND_WORD_SHORT, FindCommand.COMMAND_WORD, 
                            "keywords", "[-status]"),
                    new CommandGuide("Listing of Tasks", ListCommand.COMMAND_WORD_SHORT, ListCommand.COMMAND_WORD, 
                            "[-status]"),
                    new CommandGuide("Marking Tasks as Done", DoneCommand.COMMAND_WORD_SHORT, DoneCommand.COMMAND_WORD, 
                            "task_number"),
                    new CommandGuide("Clearing of Tasks", "", ClearCommand.COMMAND_WORD),
                    new CommandGuide("Changing of Save Directory", ChangeDirectoryCommand.COMMAND_WORD_SHORT,
                            ChangeDirectoryCommand.COMMAND_WORD, "directory_path"),
                    new CommandGuide("Opening of File", OpenFileCommand.COMMAND_WORD_SHORT, OpenFileCommand.COMMAND_WORD,
                            "file_path"),
                    new CommandGuide("Help Window Display", HelpCommand.COMMAND_WORD_SHORT, HelpCommand.COMMAND_WORD),
                    new CommandGuide("Exiting from Taskle", "", ExitCommand.COMMAND_WORD)));

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
        setupShortcutCol();
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

    private void setupShortcutCol() {
        TableColumn<CommandGuide, String> shortcutCol = 
                new TableColumn<>(COLUMN_NAME_SHORTCUT);
        bindShortcutColString(shortcutCol);
        setShortcutColStyle(shortcutCol);
        shortcutCol.setSortable(false);
        helpTable.getColumns().add(shortcutCol);
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
     * 
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
     * 
     * @param formatCol Format Column for help window
     */
    private void bindShortcutColString(TableColumn<CommandGuide, String> formatCol) {
        formatCol.setCellValueFactory(
                new Callback<CellDataFeatures<CommandGuide, String>, 
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<CommandGuide, String> param) {
                CommandGuide commandGuide = param.getValue();
                String shortcutCommand = commandGuide.getShortcutCommand();
                return getShortcutColString(shortcutCommand);
            }
        });
    }
    
    /**
     * This method binds the Strings for format columns to the CommandGuide
     * objects.
     * 
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
        nameCol.prefWidthProperty().bind(helpTable.widthProperty().multiply(0.27));
        nameCol.setResizable(false);
    }
    
    private void setShortcutColStyle(TableColumn<CommandGuide, String> shortcutCol) {
        shortcutCol.prefWidthProperty().bind(helpTable.widthProperty().multiply(0.13));
        shortcutCol.setResizable(false);
    }

    private void setFormatColStyle(TableColumn<CommandGuide, String> formatCol) {
        formatCol.prefWidthProperty().bind(helpTable.widthProperty().multiply(0.6));
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

    private ObservableValue<String> getShortcutColString(String shortcutCommand) {
        return new SimpleStringProperty(shortcutCommand);
    }
    
    public void show() {
        dialogStage.showAndWait();
    }
}