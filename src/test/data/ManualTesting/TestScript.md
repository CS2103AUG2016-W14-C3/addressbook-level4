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
3. Feedback message at the top shows "storage file has been changed".

### Test Case 2: Adding a float task
Purpose: Test whether adding a task with just the name works.  
Test data: `taskName = "Buy eggs"` <br><br>
Steps:
1. Click on command box
2. Type `add` `taskName`
3. Press <kbd>enter</kbd>

Expected Result: 
1. A new task called `taskName` is added.
2. Task is labelled with the pending color code.
3. Feedback message at the top shows "Added new task: `taskName`"

### 3. Adding a deadline task
Purpose: Test whether adding a deadline with name and date works.  
Test data: `taskName = "Submit project proposal"`, `taskDate = "tmr"`<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` by `taskDate`
3. Press <kbd>enter</kbd>

Expected Result: 
1. A new deadline called `taskName` is added.
2. The deadline is also added with the deadline date as the following day.
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added new task: `taskName` by ..."


### 3. Adding an event task
Purpose: Test whether adding an event with name and dates works.  
Test data: `taskName = "Class outing"`, `startDate = "25 dec 10am"`, `endDate = "6pm"`<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` from `startDate` to `endDate`
3. Press <kbd>enter</kbd>

Expected Result: 
1. A new event called `taskName` is added.
2. The event is also added with the date as "25 Dec, 10:00AM to 6:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added new task: `taskName` from ..."

### 4. Adding a task with reminder
Purpose: Test whether adding a task with name and reminder works.  
Test data: `taskName = "Finish Assignment 4"`, `remindDate = "11 Nov 2pm"`<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` remind `remindDate`
3. Press <kbd>enter</kbd>

Expected Result: 
1. A new task called `taskName` is added.
2. The reminder date is set as "11 Nov, 2:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top shows "Added new task: `taskName` reminder on 11 Nov, 2:00PM"

### 5. Removing a task
Purpose: Test whether removing a task works.  
Test data: `taskIndex = "5"` `taskName = Buy bread & Cookies`<br><br>
Steps:
1. Click on command box
2. Type `remove` `taskIndex` 
3. Press <kbd>enter</kbd>

Expected Result: 
1. The task with number `taskIndex` and name `taskName` is removed.
2. Feedback message at the top says removed task(s): `taskIndex`.

### 6. Marking a task as Done
Purpose: Test whether done command works.  
Test data: `taskIndex = "7"` `taskName = Buy eggs`<br><br>
Steps:
1. Click on command box
2. Type `done` `taskIndex` 
3. Press <kbd>enter</kbd>

Expected Result: 
1. The task with number `taskIndex` and name `taskName` is removed from the current view.
2. Feedback message at the top shows Task completed!`.