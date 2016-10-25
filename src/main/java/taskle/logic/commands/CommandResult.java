package taskle.logic.commands;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;
    private final boolean isValid;

    public CommandResult(String feedbackToUser, boolean wasValidCommand) {
        assert feedbackToUser != null;
        this.feedbackToUser = feedbackToUser;
        this.isValid = wasValidCommand;
    }
    
    public String getFeedback() {
        return feedbackToUser;
    }
    
    public boolean wasValid() {
        return isValid;
    }

}
