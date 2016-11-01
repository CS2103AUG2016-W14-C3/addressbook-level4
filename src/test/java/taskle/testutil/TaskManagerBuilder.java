package taskle.testutil;

import taskle.model.TaskManager;
import taskle.model.task.Task;

/**
 * A utility class to help with building TaskManager objects.
 * Example usage: <br>
 *     {@code TaskManager taskManager = new TaskManagerBuilder().withTask("Dinner", "Lunch").withTag("Friend").build();}
 */
public class TaskManagerBuilder {

    private TaskManager taskManager;

    public TaskManagerBuilder(TaskManager addressBook){
        this.taskManager = addressBook;
    }

    public TaskManagerBuilder withPerson(Task task) {
        taskManager.addTask(task);
        return this;
    }

    public TaskManager build(){
        return taskManager;
    }
}
