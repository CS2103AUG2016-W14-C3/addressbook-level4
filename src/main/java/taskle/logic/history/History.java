package taskle.logic.history;

import java.util.Stack;

import taskle.logic.commands.Command;

/**
 * History of mutating commands
 */
public class History {

    private Stack<Command> recentCommands;
    
    public History() {
        recentCommands = new Stack<Command>();
    }
    
    /**
     * Inserts most recent command into history
     * @param command Most recent command
     */
    public void insert(Command command) {
        recentCommands.push(command);
    }
    
    /**
     * Removes most recent command from history
     * @return Most recent command
     */
    public Command remove() {
        return recentCommands.pop();
    }
    
    /**
     * Checks if History contains any recent commands
     * @return true if no recent commands
     */
    public boolean isEmpty() {
        return recentCommands.isEmpty();
    }
    
}
