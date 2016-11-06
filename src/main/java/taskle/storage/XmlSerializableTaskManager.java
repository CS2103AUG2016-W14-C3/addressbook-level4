package taskle.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import taskle.model.ReadOnlyTaskManager;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.TaskList;

// An Immutable TaskManager that is serializable to XML format
@XmlRootElement(name = "taskmanager")
public class XmlSerializableTaskManager implements ReadOnlyTaskManager {

    @XmlElement
    private List<XmlAdaptedTask> tasks;

    {
        tasks = new ArrayList<>();
    }

    // Empty constructor required for marshalling
    public XmlSerializableTaskManager() {}

    // Conversion
    public XmlSerializableTaskManager(ReadOnlyTaskManager src) {
        tasks.addAll(src.getTaskList().stream().map(XmlAdaptedTask::new).collect(Collectors.toList()));
    }

    @Override
    public TaskList getUniqueTaskList() {
        TaskList lists = new TaskList();
        for (XmlAdaptedTask p : tasks) {
            lists.add(p.toModelType());
        }
        return lists;
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return tasks.stream().map(p -> {
            return p.toModelType();
        }).collect(Collectors.toCollection(ArrayList::new));
    }

}
