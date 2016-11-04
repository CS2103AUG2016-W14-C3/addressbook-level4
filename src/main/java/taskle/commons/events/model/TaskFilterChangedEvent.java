package taskle.commons.events.model;

import taskle.commons.events.BaseEvent;
import taskle.commons.util.StatusFormatUtil;

//@@author A0141780J
/** Indicates the filters in TaskList has changed.
 *  When is showing all, showing pending, done and overdue are all false.
 */
public class TaskFilterChangedEvent extends BaseEvent {

    public boolean showAll;
    public boolean showPending;
    public boolean showDone;
    public boolean showOverdue;

    public TaskFilterChangedEvent(boolean showPending, boolean showDone,
            boolean showOverdue){
        if (showPending && showDone && showOverdue) {
            showAll = true;
            return;
        }
        
        this.showPending = showPending;
        this.showDone = showDone;
        this.showOverdue = showOverdue;
    }

    @Override
    public String toString() {
        String message = StatusFormatUtil.getFormattedFilters(
                showPending, showDone, showOverdue);
        return "Filter set to: " + message;
    }
    
    public boolean isPendingShown() {
        return showPending;
    }
    
    public boolean isOverdueShown() {
        return showOverdue;
    }
    
    public boolean isDoneShown() {
        return showDone;
    }
    
    public boolean isAllShown() {
        return showAll;
    }
}
