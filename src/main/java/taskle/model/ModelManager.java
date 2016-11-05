package taskle.model;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.transformation.FilteredList;
import taskle.commons.core.ComponentManager;
import taskle.commons.core.LogsCenter;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.events.model.TaskFilterChangedEvent;
import taskle.commons.events.model.TaskManagerChangedEvent;
import taskle.commons.events.storage.StorageMenuItemRequestEvent;
import taskle.commons.exceptions.DataConversionException;
import taskle.commons.util.StorageUtil;
import taskle.commons.util.StorageUtil.OperationType;
import taskle.commons.util.StringUtil;
import taskle.logic.commands.ChangeDirectoryCommand;
import taskle.logic.commands.OpenFileCommand;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.ReadOnlyTask.Status;
import taskle.model.task.Task;
import taskle.model.task.TaskList.TaskNotFoundException;

/**
 * Represents the in-memory model of the task manager data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskManager taskManager;
    private final FilteredList<Task> filteredTasks;

    private Stack<TaskManager> taskManagerHistory = new Stack<TaskManager>();
    private Stack<TaskManager> redoTaskManagerHistory = new Stack<TaskManager>();
    
    // Filter variables
    private boolean isDoneShown = false;
    private boolean isPendingShown = true;
    private boolean isOverdueShown = true;

    /**
     * Initializes a ModelManager with the given TaskManager
     * TaskManager and its variables should not be null
     */
    public ModelManager(TaskManager src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with task manager: " + src + " and user prefs " + userPrefs);

        taskManager = new TaskManager(src);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
        updateFilteredListWithStatuses();
    }

    public ModelManager() {
        this(new TaskManager(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskManager initialData, UserPrefs userPrefs) {
        taskManager = new TaskManager(initialData);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
        updateFilteredListWithStatuses();
    }

    @Override
    public void resetData(ReadOnlyTaskManager newData) {
        taskManager.resetData(newData);
        indicateTaskManagerChanged();
    }

    @Override
    public ReadOnlyTaskManager getTaskManager() {
        return taskManager;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskManagerChanged() {
        raise(new TaskManagerChangedEvent(taskManager));
    }

    //@@author A0140047U
    /** Stores current TaskManager state */
    @Override
    public synchronized void storeTaskManager(String command) {
        try {
            if (command.equals(ChangeDirectoryCommand.COMMAND_WORD)) {
                StorageUtil.storeConfig(OperationType.CHANGE_DIRECTORY);
                taskManagerHistory.push(null);
            } else if (command.equals(OpenFileCommand.COMMAND_WORD)) {
                StorageUtil.storeConfig(OperationType.OPEN_FILE);
                taskManagerHistory.push(null);
            } else {
                StorageUtil.storeConfig(null);
                taskManagerHistory.push(new TaskManager(taskManager));
            }
            redoTaskManagerHistory.clear();
            StorageUtil.clearRedoConfig();
            
        } catch (DataConversionException e) {
            e.printStackTrace();
        }
    }
    
    // Restores recently saved TaskManager state
    @Override
    public synchronized boolean restoreTaskManager() {
        
        try {
            if (StorageUtil.isConfigHistoryEmpty() && taskManagerHistory.isEmpty()) {
                return false;
            } else if (!taskManagerHistory.isEmpty() && taskManagerHistory.peek() == null) {
                StorageUtil.restoreConfig(); 
                taskManagerHistory.pop();
                redoTaskManagerHistory.push(null);
                return true;
            } else {
                TaskManager recentTaskManager = taskManagerHistory.pop();
                redoTaskManagerHistory.push(new TaskManager(taskManager));
                this.resetData(recentTaskManager);
                return true;
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Reverts changes made from restoring recently saved TaskManager state
    @Override
    public synchronized boolean revertTaskManager() {
         try {
            if (StorageUtil.isRedoConfigHistoryEmpty() && redoTaskManagerHistory.isEmpty()) {
                return false;
            } else if (!redoTaskManagerHistory.isEmpty() && redoTaskManagerHistory.peek() == null) {
                StorageUtil.revertConfig();
                redoTaskManagerHistory.pop();
                taskManagerHistory.push(null);
                return true;
            } else {
                TaskManager redoTaskManager = redoTaskManagerHistory.pop();
                taskManagerHistory.push(new TaskManager(taskManager));
                this.resetData(redoTaskManager);
                return true;
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public synchronized void rollBackTaskManager(boolean isStorageOperation) {

        taskManagerHistory.pop();
        if (isStorageOperation) {
            StorageUtil.undoConfig();
        }
    }
    
    
    @Override
    @Subscribe
    public void handleStorageMenuItemRequestEvent(StorageMenuItemRequestEvent smire) {
        if (smire.isValid()) {
            storeTaskManager(smire.getCommand());
        } else {
            rollBackTaskManager(true);
        }
    }
    
    //@@author

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.removeTask(target);
        indicateTaskManagerChanged();
    }
    //@@author A0139402M
    @Override

    public synchronized void editTask(int index, Name newName) throws TaskNotFoundException {
        int sourceIndex = filteredTasks.getSourceIndex(index);
        taskManager.editTask(sourceIndex, newName);;
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized void editTaskDate(int index, List<Date> dates) throws TaskNotFoundException{
        int sourceIndex = filteredTasks.getSourceIndex(index);
        taskManager.editTaskDate(sourceIndex, dates);
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized String editTaskRemindDate(int index, Date date) throws TaskNotFoundException{
        int sourceIndex = filteredTasks.getSourceIndex(index);
        String message = taskManager.editTaskRemindDate(sourceIndex, date);
        indicateTaskManagerChanged();
        return message;
    }
    
    @Override
    public synchronized List<Task> verifyRemindDate(Date currentDateTime) {
        return taskManager.verifyReminder(currentDateTime);
    }
    
    @Override
    public synchronized void dismissReminder(Date currentDateTime) {
        taskManager.dismissReminder(currentDateTime);
        indicateTaskManagerChanged();
    }
    
    //@@author A0125509H
    @Override
    public synchronized void doneTask(int index, boolean targetDone) throws TaskNotFoundException {
        int sourceIndex = filteredTasks.getSourceIndex(index - 1);
        taskManager.doneTask(sourceIndex, targetDone);
        updateFilteredListWithStatuses();
        indicateTaskManagerChanged();
    }
    //@@author
    
    @Override
    public synchronized void addTask(Task task) {
        taskManager.addTask(task);
        resetFilters();
        updateFilteredListWithStatuses();
        indicateTaskManagerChanged();
    }

    //@@author A0141780J
    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(filteredTasks);
    }
    
    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }
    
    @Override
    public void updateFilteredListWithStatuses() {
        filteredTasks.setPredicate(getStatusPredicate());
    }
    
    @Override
    public void updateFilters(Set<String> keywords){
        updateFilteredListFindKeywords(keywords);
    }
    
    @Override
    public void updateFilters(Set<String> keywords, boolean isPendingShown, 
                              boolean isDoneShown, boolean isOverdueShown){
        this.isPendingShown = isPendingShown;
        this.isDoneShown = isDoneShown;
        this.isOverdueShown = isOverdueShown;
        updateFilteredListFindKeywords(keywords);
    }
    
    @Override
    public void updateFilters(
            boolean isPendingShown, boolean isDoneShown, boolean isOverdueShown) {
        this.isPendingShown = isPendingShown;
        this.isDoneShown = isDoneShown;
        this.isOverdueShown = isOverdueShown;
        raise(new TaskFilterChangedEvent(isPendingShown, isDoneShown, isOverdueShown));
        updateFilteredListWithStatuses();
    }
    
    private void resetFilters() {
        this.isPendingShown = true;
        this.isDoneShown = false;
        this.isOverdueShown = true;
        raise(new TaskFilterChangedEvent(isPendingShown, isDoneShown, isOverdueShown));
        updateFilteredListWithStatuses();
    }
    
    private void updateFilteredListFindKeywords(Set<String> keywords) {
        Expression keywordExpression = new PredicateExpression(new NameQualifier(keywords));
        Predicate<Task> statusPred = getStatusPredicate();
        Predicate<Task> combinedPred = statusPred.and(keywordExpression::satisfies);
        filteredTasks.setPredicate(combinedPred);
    }
    
    /**
     * Returns the predicate to use for filtering as specified by 
     * the show status boolean fields.
     * @return
     */
    private Predicate<Task> getStatusPredicate() {
        Predicate<Task> basePred = t -> false;
        Predicate<Task> pendingPred = t -> t.getStatus() == Status.PENDING
                || t.getStatus() == Status.FLOAT;
        Predicate<Task> donePred = t -> t.getStatus() == Status.DONE;
        Predicate<Task> overduePred = t -> t.getStatus() == Status.OVERDUE;
        
        if (isPendingShown) {
            basePred = basePred.or(pendingPred);
        }
        
        if (isDoneShown) {
            basePred = basePred.or(donePred);
        }
        
        if (isOverdueShown) {
            basePred = basePred.or(overduePred);
        }
        
        return basePred;
    }
    //@@author

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);
        String toString();
    }

    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;

        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            return nameKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getName().fullName, keyword))
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }

}
