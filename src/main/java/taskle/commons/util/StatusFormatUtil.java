package taskle.commons.util;

public class StatusFormatUtil {
    //@@author A0141780J
    /**
     * Returns a formatted string for filters as specified.
     * 
     * @param showPending whether to show or filter pending
     * @param showDone whether to show or filter done
     * @param showOverdue whether to show or filter overdue
     * @return a formatted String, filters are delimited by commas
     */
    public static String getFormattedFilters(
            boolean showPending, boolean showDone, boolean showOverdue) {
        String[] messageArray = new String[] {
                "Not Pending", "Not Done", "Not Overdue"
        };
        
        if (showPending) {
            messageArray[0] = "Pending";
        }
        
        if (showDone) {
            messageArray[1] = "Done";
        }
        
        if (showOverdue) {
            messageArray[2] = "Overdue";
        }
        
        String message = String.join(", ", messageArray);
        
        return message;
    }
    
}
