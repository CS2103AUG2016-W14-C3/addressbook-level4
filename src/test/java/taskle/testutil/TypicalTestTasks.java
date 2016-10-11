package taskle.testutil;

import java.util.Calendar;
import java.util.Date;

import taskle.commons.exceptions.IllegalValueException;
import taskle.model.TaskManager;
import taskle.model.tag.UniqueTagList;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;
import taskle.model.task.UniqueTaskList;

/**
 *
 */
public class TypicalTestTasks {

    public Task attendMeeting, buyMilk, createPlan, deliverGoods, eatDinner, flyKite, goConcert, helpFriend, interview,
        charityEvent, assignmentDeadline, finalExams;
    
    private UniqueTagList stubTagList = new UniqueTagList();
    
    public TypicalTestTasks() {
        try {
            attendMeeting =  new FloatTask(new Name("Attend Meeting"), stubTagList);
            buyMilk = new FloatTask(new Name("Buy milk"), stubTagList);
            createPlan = new FloatTask(new Name("Create Plan"), stubTagList);
            deliverGoods = new FloatTask(new Name("Deliver milk"), stubTagList);
            eatDinner = new FloatTask(new Name("Eat dinner"), stubTagList);
            flyKite = new FloatTask(new Name("Fly kite"), stubTagList);
            goConcert = new FloatTask(new Name("Go Concert"), stubTagList);

            //Manually added
            helpFriend = new FloatTask(new Name("Help friend"), stubTagList);
            interview = new FloatTask(new Name("Interview with Google"), stubTagList);
            
            //Events and deadlines
            prepareTimedTasks();
        } catch (IllegalValueException e) {
            e.printStackTrace();
            assert false : "not possible";
        }
    }
    
    private void prepareTimedTasks() throws IllegalValueException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 9, 10, 17, 0);
        Date startDate = calendar.getTime();
        calendar.set(2016, 9, 10, 17, 0);
        Date endDate = calendar.getTime();
        charityEvent = new EventTask(new Name("Charity Event"), 
                startDate, endDate, stubTagList);
        calendar.set(2016, 10, 10, 23, 59);
        Date deadline = calendar.getTime();
        assignmentDeadline = new DeadlineTask(new Name("Assignment due"),
                deadline, stubTagList);
        
        // Manually added
        calendar.set(2016, 11, 01, 12, 00);
        startDate = calendar.getTime();
        calendar.set(2016, 11, 01, 14, 00);
        endDate = calendar.getTime();
        finalExams = new EventTask(new Name("Final exams"), 
                startDate, endDate, stubTagList);
    }

    public void loadTaskManagerWithSampleData(TaskManager ab) {

        try {
            ab.addTask(attendMeeting.copy());
            ab.addTask(buyMilk.copy());
            ab.addTask(createPlan.copy());
            ab.addTask(deliverGoods.copy());
            ab.addTask(eatDinner.copy());
            ab.addTask(flyKite.copy());
            ab.addTask(goConcert.copy());
            ab.addTask(charityEvent.copy());
            ab.addTask(assignmentDeadline.copy());
        } catch (UniqueTaskList.DuplicateTaskException e) {
            assert false : "not possible";
        }
    }

    public Task[] getTypicalTasks() {
        return new Task[]{attendMeeting, buyMilk, createPlan, deliverGoods, eatDinner, flyKite, goConcert, charityEvent, assignmentDeadline};
    }

    public TaskManager getTypicalTaskManager(){
        TaskManager ab = new TaskManager();
        loadTaskManagerWithSampleData(ab);
        return ab;
    }
    
}

