package taskle.testutil;

import java.util.Calendar;
import java.util.Date;

import taskle.commons.exceptions.IllegalValueException;
import taskle.model.TaskManager;
import taskle.model.task.DeadlineTask;
import taskle.model.task.EventTask;
import taskle.model.task.FloatTask;
import taskle.model.task.Name;
import taskle.model.task.Task;

/**
 *
 */
public class TypicalTestTasks {

    public Task attendMeeting, buyMilk, createPlan, deliverGoods, eatDinner, flyKite, goConcert, helpFriend, interview,
        charityEvent, assignmentDeadline, finalExams, industryTalk;
        
    public TypicalTestTasks() {
        try {
            attendMeeting =  new FloatTask(new Name("Attend Meeting"));
            buyMilk = new FloatTask(new Name("Buy milk"));
            createPlan = new FloatTask(new Name("Create Plan"));
            deliverGoods = new FloatTask(new Name("Deliver milk"));
            eatDinner = new FloatTask(new Name("Eat dinner"));
            flyKite = new FloatTask(new Name("Fly kite"));
            goConcert = new FloatTask(new Name("Go Concert"));

            //Manually added
            helpFriend = new FloatTask(new Name("Help friend"));
            interview = new FloatTask(new Name("Interview with Google"));
            
            //Events and deadlines
            prepareTimedTasks();
            prepareRemindTasks();
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
                startDate, endDate);
        calendar.set(2016, 10, 10, 23, 59);
        Date deadline = calendar.getTime();
        assignmentDeadline = new DeadlineTask(new Name("Assignment due"),
                deadline);
        
        // Manually added
        calendar.set(2016, 11, 01, 12, 00);
        startDate = calendar.getTime();
        calendar.set(2016, 11, 01, 14, 00);
        endDate = calendar.getTime();
        finalExams = new EventTask(new Name("Final exams"), 
                startDate, endDate);
    }
    
    private void prepareRemindTasks() throws IllegalValueException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 11, 01, 12, 00);
        Date remindDate = calendar.getTime();
        industryTalk = new FloatTask(new Name("Industry Talk"), remindDate);
    }

    public void loadTaskManagerWithSampleData(TaskManager ab) {

        ab.addTask(attendMeeting.copy());
        ab.addTask(buyMilk.copy());
        ab.addTask(createPlan.copy());
        ab.addTask(deliverGoods.copy());
        ab.addTask(eatDinner.copy());
        ab.addTask(flyKite.copy());
        ab.addTask(goConcert.copy());
        ab.addTask(charityEvent.copy());
        ab.addTask(assignmentDeadline.copy());
        ab.addTask(industryTalk.copy());
    }

    public Task[] getTypicalTasks() {
        return new Task[]{charityEvent, assignmentDeadline, attendMeeting, buyMilk, 
                createPlan, deliverGoods, eatDinner, flyKite, goConcert, industryTalk};
    }

    public TaskManager getTypicalTaskManager(){
        TaskManager ab = new TaskManager();
        loadTaskManagerWithSampleData(ab);
        return ab;
    }
    
}

