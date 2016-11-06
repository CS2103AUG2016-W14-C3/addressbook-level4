package taskle.model;


import java.util.List;

import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList;

// Unmodifiable view of an task manager
public interface ReadOnlyTaskManager {

    TaskList getUniqueTaskList();

    // Returns an unmodifiable view of tasks list
    List<ReadOnlyTask> getTaskList();

}