package taskle.logic.commands;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;
    private final boolean isSuccessful;

    public CommandResult(String feedbackToUser, boolean isSuccessful) {
        assert feedbackToUser != null;
        this.feedbackToUser = feedbackToUser;
        this.isSuccessful = isSuccessful;
    }
    
    public String getFeedback() {
        return feedbackToUser;
    }
    
    public boolean isSuccessful() {
        return isSuccessful;
    }

}
