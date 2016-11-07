# Manual Testing

### Prerequisite for all tests: 

1. Application must be started and running
2. Tests should run in sequence from Test Case 1 to the last Test Case. 


### Test Case 1: Loading the sample data file
Purpose: Test whether loading data file works.  
Test data: `filePath = src\test\data\ManualTesting\SampleData.xml` <br><br>
Steps:

1. Click on command box
2. Type `open` `filePath`
3. Press <kbd>enter</kbd>

Expected Result: 

1. Taskle switches to using SampleData.xml.
2. The new file path is reflected in the status bar footer.
3. Feedback message at the top shows "Storage File has been changed.".

<br>
### Test Case 2: Adding a float task
Purpose: Test whether adding a task with just the name works.  
Test data: `taskName = Buy eggs` <br><br>
Steps:

1. Click on command box
2. Type **`add`** `taskName`
3. Press <kbd>enter</kbd>

Expected Result: 

1. A new task called `taskName` is added.
2. Task is labelled with the pending color code.
3. Feedback message at the top shows "Added New Task: `taskName`"

<br>
### Test Case 3. Adding a deadline task
Purpose: Test whether adding a deadline with name and date works.  
Test data: `taskName = Submit project proposal` ; `taskDate = tmr`<br><br>
Steps:

1. Click on command box
2. Type **`add`** `taskName` **`by`** `taskDate`
3. Press <kbd>enter</kbd>

Expected Result: 

1. A new deadline called `taskName` is added.
2. The deadline is also added with the deadline date as the following day.
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added New Task: `taskName` by ..."

<br>
### Test Case 4. Adding an event task
Purpose: Test whether adding an event with name and dates works.  
Test data: `taskName = Class outing` ; `startDate = 25 dec 10am` ; `endDate = 6pm`<br><br>
Steps:

1. Click on command box
2. Type **`add`** `taskName` **`from`** `startDate` **`to`** `endDate`
3. Press <kbd>enter</kbd>

Expected Result: 

1. A new event called `taskName` is added.
2. The event is also added with the date as "25 Dec, 10:00AM to 6:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added New Task: `taskName` from ..."

<br>
### Test Case 5. Adding a task with reminder
Purpose: Test whether adding a task with name and reminder works.  
Test data: `taskName = Finish Assignment 4` ; `remindDate = 11 Nov 2pm`<br><br>
Steps:

1. Click on command box
2. Type **`add`** `taskName` **`remind`** `remindDate`
3. Press <kbd>enter</kbd>

Expected Result:

1. A new task called `taskName` is added.
2. The reminder date is set as "11 Nov, 2:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added New Task: `taskName` Reminder on 11 Nov, 2:00PM"

<br>
### Test Case 6. Renaming a task
Purpose: Test whether renaming a task works.  
Test data: `taskIndex = 8` ; `newTaskName = Buy eggs and milk` ; `taskName = Buy eggs`<br><br>
Steps:

1. Click on command box
2. Type **`rename`** `taskIndex` `newTaskName` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is renamed to `newTaskName`.
2. Feedback message at the top says "Renamed Task: `taskName` -> `newTaskName`.

<br>
### Test Case 7. Reschedule a task
Purpose: Test whether rescheduling a task works.  
Test data: `taskIndex = 9` ; `taskName = Finish Assignment 4` ; `newDate = 20 nov 1pm`<br><br>
Steps:

1. Click on command box
2. Type **`reschedule`** `taskIndex` `newDate` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is rescheduled to `newDate`.
2. Feedback message at the top shows "Rescheduled Task: `taskName` -> ..."

<br>
### Test Case 8. Clear date and time for a task
Purpose: Test whether clear date and time for a task works.  
Test data: `taskIndex = 3` ; `taskName = Buy Snacks for picnic`<br><br>
Steps:

1. Click on command box
2. Type **`reschedule`** `taskIndex` **`clear`** 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The date associated with task of number `taskIndex` is removed.
2. Feedback message at the top shows "Rescheduled Task: `taskName` ... -> "

<br>
### Test Case 9. Set Reminder for a task
Purpose: Test whether setting reminders for a task works.  
Test data: `taskIndex = 5` ; `taskName = Buy Bread & Cookies` ; `remindDate = 7 Nov 5pm`<br><br>
Steps:

1. Click on command box
2. Type **`remind`** `taskIndex` `remindDate` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` now has a reminder of `remindDate`.
2. Feedback message at the top shows "Set Reminder Date: `taskName` -> ..."
3. Within the next minute a notification for the reminder will appear at the bottom right of the screen.

**Note: Only proceed to test case 10 after verifying that the notification has appeared.**

<br>
### Test Case 10. Clear Reminder Date for a task
Purpose: Test whether clear reminder date for a task works.  
Test data: `taskIndex = 5` ; `taskName = Buy Bread & Cookies`<br><br>
Steps:

1. Click on command box
2. Type **`remind`** `taskIndex` **`clear`** 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The reminder date associated with task of number `taskIndex` is removed.
2. Feedback message at the top shows "Set Reminder Date: `taskName` -> "

<br>
### Test Case 11. Removing a task
Purpose: Test whether removing a task works.  
Test data: `taskIndex = 9` ; `taskName = Buy Snack for picnic`<br><br>
Steps:

1. Click on command box
2. Type **`remove`** `taskIndex` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is removed.
2. Feedback message at the top says "Removed Task(s): `taskIndex`"

<br>
### Test Case 12. Removing multiple tasks
Purpose: Test whether removal of multiple tasks works.  
Test data: `taskIndices = 2 5 7`<br><br>
Steps:

1. Click on command box
2. Type **`remove`** `taskIndices` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The tasks with numbers 2, 5 and 7 are removed.
2. Feedback message at the top says "Removed Task(s): `taskIndices`"

<br>
### Test Case 13. Marking a task as Done
Purpose: Test whether done command works.  
Test data: `taskIndex = 2` ; `taskName = Finish Assignment 4`<br><br>
Steps:

1. Click on command box
2. Type **`done`** `taskIndex` 
3. Press <kbd>enter</kbd>

Expected Result:

1. The task with number `taskIndex` and name `taskName` is marked as done and removed from the current view.
2. Feedback message at the top shows "Task Completed!".

<br>
### Test Case 14. Undo a command
Purpose: Test whether undo command reverts the previous command.  
Test data: `taskIndex = 2` ; `taskName = Finish Assignment 4`<br><br>
Steps:

1. Click on command box
2. Type **`undo`**
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` should be unmarked as done and appear back in the current view.
2. Feedback message at the top shows "Restored previous command.".

<br>
### Test Case 15. Redo a command
Purpose: Test whether redo command reverts the previously issued undo command.  
Test data: `taskIndex = 2` ; `taskName = Finish Assignment 4`<br><br>
Steps:

1. Click on command box
2. Type **`redo`**
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` should be marked as done and removed from the current view.
2. Feedback message at the top shows "Redo previous command.".

<br>
### Test Case 16. Finding a task by 1 keyword and 1 status
Purpose: Test whether find by 1 keyword and 1 status works  
Test data: `keyword = assignment` ; `status = -done `<br><br>
Steps:

1. Click on command box
2. Type **`find`** `keyword` `status`
3. Press <kbd>enter</kbd>

Expected Result: 

1. All tasks with `keyword` in their names are listed.
2. The listed tasks are all labelled with the done color code.
3. Feedback message at the top shows "4 task(s) listed!".
4. Status display panel shows done labels.

<br>
### Test Case 17. Finding a task by 2 keywords and 2 statuses
Purpose: Test whether find by more than 1 keyword and 1 status works.   
Test data: `keywords = buy meeting` ; `statuses = -done -pending`<br><br>
Steps:

1. Click on command box
2. Type **`find`** `keywords` `statuses`
3. Press <kbd>enter</kbd>

Expected Result: 

1. All tasks with any of the `keywords` in their names are listed.
2. The listed tasks are all labelled with the done or pending color codes.
3. Feedback message at the top shows "8 task(s) listed!".
4. Status display panel shows pending and done labels.

<br>
### Test Case 18. Listing tasks
Purpose: Test whether list command works.<br><br>  
Steps:

1. Click on command box
2. Type **`list`**
3. Press <kbd>enter</kbd>

Expected Result: 

1. All tasks that are overdue or pending are listed.
2. The listed tasks are all labelled with the overdue or pending color codes.
3. Feedback message at the top shows "Listed: Pending, Not Done, Overdue tasks".
4. Status display panel shows pending and overdue labels.

<br>
### Test Case 19. Listing tasks by status
Purpose: Test whether list command works with status.   
Test data: `status = -done`<br><br>
Steps:

1. Click on command box
2. Type **`list`** `status`
3. Press <kbd>enter</kbd>

Expected Result: 

1. All tasks that are done are listed.
2. The listed tasks are all labelled with the done color codes.
3. Feedback message at the top shows "Listed: Not Pending, Done, Not Overdue tasks".
4. Status display panel shows done label only.

<br>
### Test Case 20. Open Help Window
Purpose: Test whether help command works.<br><br>   
Steps:

1. Click on command box
2. Type **`help`**
3. Press <kbd>enter</kbd>

Expected Result: 

1. Help window is shown.

<br>
### Test Case 21. Change Save Directory
Purpose: Test whether change directory command works.  
Test data: `filePath = src\test\data` <br><br>
Steps:

1. Click on command box
2. Type **`save`** `filePath`
3. Press <kbd>enter</kbd>

Expected Result: 

1. The new file path is reflected in the status bar footer.
2. Feedback message at the top shows "Storage Directory has been changed to...".

<br>
### Test Case 22. Clear task manager
Purpose: Test whether help command works.<br><br>
Steps:

1. Click on command box
2. Type **`clear`**
3. Press <kbd>enter</kbd>

Expected Result: 

1. All tasks are cleared from Taskle.
2. Feedback message at the top shows "Taskle has been Cleared!".

<br>
### Test Case 23. Shortcut command for add
Purpose: Test whether shortcut command for add works. 
Test data: `taskName = Buy eggs and milk` ; `taskDate = tmr 7pm` <br><br>
Steps:

1. Click on command box
2. Type **`a`** `taskName` **`by`** `taskDate`
3. Press <kbd>enter</kbd>

Expected Result: 

1. A new deadline called `taskName` is added.
2. The deadline is also added with the deadline date as the following day 7pm.
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added New Task: `taskName` by ..."

<br>
### Test Case 24. Shortcut command for rename
Purpose: Test whether shortcut command for rename works.   
Test data: `taskIndex = 1` ; `newTaskName = Buy eggs, milk and cookies` ;  `taskName = Buy eggs and milk`<br><br>
Steps:

1. Click on command box
2. Type **`rn`** `taskIndex` `newTaskName`
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is renamed to `newTaskName`.
2. Feedback message at the top says "Renamed Task: `taskName` -> `newTaskName`.

<br>
### Test Case 25. Shortcut command for reschedule
Purpose: Test whether rescheduling a task works.  
Test data: `taskIndex = 1` ; `taskName = Buy eggs, milk and cookies` ; `newDate = 30 nov 1pm`<br><br>
Steps:

1. Click on command box
2. Type **`rs`** `taskIndex` `newDate` 
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is rescheduled to `newDate`.
2. Feedback message at the top shows "Rescheduled Task: `taskName` -> ..."

<br>
### Test Case 26. Shortcut command for remove
Purpose: Test whether shortcut command for remove works.  
Test data: `taskIndex = 1` ; `taskName = Buy eggs, milk and cookies`<br><br>
Steps:

1. Click on command box
2. Type **`rm`** `taskIndex`
3. Press <kbd>enter</kbd>

Expected Result: 

1. The task with number `taskIndex` and name `taskName` is removed.
2. Feedback message at the top says "Removed Task(s): `taskIndex`"

<br>
### Test Case 27. Exit task manager
Purpose: Test whether exit command works.<br><br>
Steps:

1. Click on command box
2. Type **`exit`**
3. Press <kbd>enter</kbd>

Expected Result:

1. Taskle is closed.