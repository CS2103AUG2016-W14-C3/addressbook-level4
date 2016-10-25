package taskle.model;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import taskle.commons.core.ComponentManager;
import taskle.commons.core.LogsCenter;
import taskle.commons.core.UnmodifiableObservableList;
import taskle.commons.events.model.TaskManagerChangedEvent;
import taskle.commons.util.StringUtil;
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
    
    // Filter variables
    private boolean showDone = false;
    private boolean showPending = true;
    private boolean showOverdue = true;

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

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.removeTask(target);
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized void editTask(int index, Name newName) throws TaskNotFoundException {
        int sourceIndex = filteredTasks.getSourceIndex(index - 1);
        taskManager.editTask(sourceIndex, newName);;
        indicateTaskManagerChanged();
    }
    
    @Override
    public void editTaskDate(int index, List<Date> dates) throws TaskNotFoundException{
        int sourceIndex = filteredTasks.getSourceIndex(index - 1);
        taskManager.editTaskDate(sourceIndex, dates);
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized void doneTask(int index, boolean targetDone) throws TaskNotFoundException {
        int sourceIndex = filteredTasks.getSourceIndex(index - 1);
        taskManager.doneTask(sourceIndex, targetDone);
        updateFilteredListWithStatuses();
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized void unDoneTask(Task task) {
        taskManager.unDoneTask(task);
        updateFilteredListWithStatuses();
    }
    
    @Override
    public synchronized void addTask(Task task) {
        taskManager.addTask(task);
        resetFilters();
        updateFilteredListWithStatuses();
        indicateTaskManagerChanged();
    }

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
    public void updateFilters(Set<String> keywords, boolean pending, boolean done, boolean overdue){
        this.showPending = pending;
        this.showDone = done;
        this.showOverdue = overdue;
        updateFilteredListFindKeywords(keywords);
    }
    
    @Override
    public void updateFilters(boolean pending, boolean done, boolean overdue) {
        this.showPending = pending;
        this.showDone = done;
        this.showOverdue = overdue;
        updateFilteredListWithStatuses();
    }
    
    private void resetFilters() {
        this.showPending = true;
        this.showOverdue = true;
        this.showDone = false;
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
        
        if (showPending) {
            basePred = basePred.or(pendingPred);
        }
        
        if (showDone) {
            basePred = basePred.or(donePred);
        }
        
        if (showOverdue) {
            basePred = basePred.or(overduePred);
        }
        
        return basePred;
    }

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
