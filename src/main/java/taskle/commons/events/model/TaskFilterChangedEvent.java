package taskle.commons.events.model;

import taskle.commons.events.BaseEvent;
import taskle.commons.util.StatusFormatUtil;

//@@author A0141780J
/** Indicates the filters in TaskList has changed.
 *  When is showing all, showing pending, done and overdue are all false.
 */
public class TaskFilterChangedEvent extends BaseEvent {

    public boolean isAllShown;
    public boolean isPendingShown;
    public boolean isDoneShown;
    public boolean isOverdueShown;

    public TaskFilterChangedEvent(boolean isPendingShown, boolean isDoneShown,
            boolean isOverdueShown){
        if (isPendingShown && isDoneShown && isOverdueShown) {
            isAllShown = true;
            return;
        }
        
        this.isPendingShown = isPendingShown;
        this.isDoneShown = isDoneShown;
        this.isOverdueShown = isOverdueShown;
    }

    @Override
    public String toString() {
        String message = StatusFormatUtil.getFormattedFilters(
                isPendingShown, isDoneShown, isOverdueShown);
        return "Filter set to: " + message;
    }
    
    public boolean isPendingShown() {
        return isPendingShown;
    }
    
    public boolean isOverdueShown() {
        return isOverdueShown;
    }
    
    public boolean isDoneShown() {
        return isDoneShown;
    }
    
    public boolean isAllShown() {
        return isAllShown;
    }
}
