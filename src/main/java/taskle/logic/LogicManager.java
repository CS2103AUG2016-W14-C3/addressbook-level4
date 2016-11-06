package taskle.logic;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import taskle.commons.core.ComponentManager;
import taskle.commons.core.LogsCenter;
import taskle.commons.events.storage.StorageChangeRequestEvent;
import taskle.logic.commands.Command;
import taskle.logic.commands.CommandResult;
import taskle.logic.parser.Parser;
import taskle.model.Model;
import taskle.model.ReadOnlyTaskManager;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;
import taskle.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Parser parser;
    private final Storage storage;
    
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.parser = new Parser();
        this.storage = storage;
    }

    @Override
    public CommandResult execute(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = parser.parseCommand(commandText);
        command.setData(model);
        return command.execute();
    }

    @Override
    public ObservableList<ReadOnlyTask> getFilteredTaskList() {
        return model.getFilteredTaskList();
    }
    
    //@@author A0140047U
    @Override
    public void changeDirectory(String filePath) {
        logger.info("----------------[CHANGE DIRECTORY][" + filePath + "]");
        storage.setTaskManagerFilePath(filePath);
    }
    
    //@@author A0139402M
    @Override
    public List<Task> verifyReminder(Date currentDateTime) {
        return model.verifyRemindDate(currentDateTime);
    }
    
    @Override
    public void dismissReminder(Date currentDateTime) {
        model.dismissReminder(currentDateTime);
    }
    //@@author A0140047U
    
    @Override
    public void resetModel(ReadOnlyTaskManager taskManager) {
        model.resetData(taskManager);
    }
    
    @Override
    @Subscribe
    public void handleStorageChangeRequestEvent(StorageChangeRequestEvent scre) {
        changeDirectory(scre.getDirectory());
        if (scre.getTaskManager() != null) {
            resetModel(scre.getTaskManager());
        }
    }
}
