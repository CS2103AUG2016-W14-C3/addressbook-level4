package taskle.logic.commands;


/**
 * Represents an incorrect command. Upon execution, produces some feedback to the user.
 */
public class IncorrectCommand extends Command {

    public final static String COMMAND_WORD = "Incorrect";
    public final String feedbackToUser;

    public IncorrectCommand(String feedbackToUser){
        this.feedbackToUser = feedbackToUser;
    }

    @Override
    public CommandResult execute() {
        indicateAttemptToExecuteIncorrectCommand(feedbackToUser);
        return new CommandResult(feedbackToUser, false);
    }

}

