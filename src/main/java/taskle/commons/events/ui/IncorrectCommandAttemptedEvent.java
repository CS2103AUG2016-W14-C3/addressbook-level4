package taskle.commons.events.ui;

import taskle.commons.events.BaseEvent;

// Indicates an attempt to execute an incorrect command
public class IncorrectCommandAttemptedEvent extends BaseEvent {
    
    String feedbackToUser;

    public IncorrectCommandAttemptedEvent(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String getFeedback() {
        return feedbackToUser;
    }
    
}
