package taskle.logic.history;

import java.util.Stack;

import taskle.logic.commands.Command;

/**
 * History of mutating commands
 */
public class History {

    public static final String MESSAGE_EMPTY_HISTORY = "Empty History. Nothing to Undo.";
    
    private static Stack<Command> recentCommands;
    
    public History() {
        recentCommands = new Stack<Command>();
    }
    
    /**
     * Inserts most recent command into history
     * @param command Most recent command
     */
    public static void insert(Command command) {
        recentCommands.push(command);
    }
    
    /**
     * Removes most recent command from history
     * @return Most recent command
     */
    public static Command remove() {
        return recentCommands.pop();
    }
    
    /**
     * Checks if History contains any recent commands
     * @return true if no recent commands
     */
    public static boolean isEmpty() {
        return recentCommands.isEmpty();
    }
    
}
