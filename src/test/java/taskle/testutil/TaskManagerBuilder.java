package taskle.testutil;

import taskle.commons.exceptions.IllegalValueException;
import taskle.model.TaskManager;
import taskle.model.tag.Tag;
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

    public TaskManagerBuilder withTag(String tagName) throws IllegalValueException {
        taskManager.addTag(new Tag(tagName));
        return this;
    }

    public TaskManager build(){
        return taskManager;
    }
}
