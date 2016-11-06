package taskle.model.help;
//@@author A0141780J
/**
 * Model class to encapsulate the information provided
 * by each row in the help window.
 * @author Abel
 *
 */
public class CommandGuide {
    
    // Member variables for a CommandGuide object
    private String actionName;
    private String commandWord;
    private String shortcutCommand;
    private String[] args; 
    
    /**
     * Constructor for CommandGuide
     * Asserts that name and commandWord are non-null because it is
     * constructed by custom parameters in HelpWindow always.
     * @param name Name of the action and command
     * @param commandWord command keyword
     * @param args optional arguments for command keyword
     */
    public CommandGuide(String name, String shortCommand, String commandWord, String... args) {
        assert name != null;
        assert commandWord != null;
        this.actionName = name;
        this.commandWord = commandWord;
        this.shortcutCommand = shortCommand;
        this.args = args;
    }
    
    public String getName() {
        return actionName;
    }
    
    public String getCommandWord() {
        return commandWord;
    }
    
    public String getShortcutCommand() {
        return shortcutCommand;
    }
    public String[] getArgs() {
        return args;
    }
}
