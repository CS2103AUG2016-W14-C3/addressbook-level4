package taskle.commons.util;

public class StatusFormatUtil {
    //@@author A0141780J
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
