package taskle.storage;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.ReadOnlyTask;
import taskle.model.task.Task;

/**
 * JAXB-friendly version of the Task.
 */
public class XmlAdaptedTask {

    @XmlElement(required = true)
    private String name;
    
    @XmlElement(required = true)
    private int isTaskDone;

    @XmlElement(required = false)
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date startDate;
    
    @XmlElement(required = false)
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date endDate;
    
    @XmlElement(required = false)
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date remindDate;

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedTask() {}


    /**
     * Converts a given Task into this class for JAXB use.
     *
     * @param source futu
     * re changes to this will not affect the created XmlAdaptedTask
     */
    public XmlAdaptedTask(ReadOnlyTask source) {
        name = source.getName().fullName;
        isTaskDone = source.isTaskDone() ? 1 : 0;
        Date taskRemindDate = source.getRemindDate();
        
        if(taskRemindDate != null) {
            remindDate = taskRemindDate;
        }
        
        if (source instanceof EventTask) {
            EventTask eventTask = (EventTask) source;
            endDate = eventTask.getEndDate();
            startDate = eventTask.getStartDate();
        } else if (source instanceof DeadlineTask) {
            DeadlineTask deadlineTask = (DeadlineTask) source;
            endDate = deadlineTask.getDeadlineDate();
        }
    }

    /**
     * Converts this jaxb-friendly adapted task object into the model's task object.
     *
     */
    public Task toModelType() {
        final Name name = new Name(this.name);
        Task task;
        if (startDate != null && endDate != null) {
            final Date endDate = this.endDate;
            final Date startDate = this.startDate;
            if(remindDate != null) {
                task = new EventTask(name ,startDate, endDate, remindDate);
            } else {
                task = new EventTask(name ,startDate, endDate);
            }
        } else if (endDate != null) {
            final Date endDate = this.endDate;
            if(remindDate != null) {
                task = new DeadlineTask(name, endDate, remindDate);
            } else {
                task = new DeadlineTask(name, endDate);
            }
        } else {
            if(remindDate != null) {
                task = new FloatTask(name, remindDate);
            } else {
                task = new FloatTask(name);
            }
        }
        task.setTaskDone(isTaskDone == 1);
        return task;
    }
}

