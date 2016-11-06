# Manual Testing

### 1. Loading the sample data file
Purpose: Test whether loading data file works.  
Prerequisite: Application must be started and running.  
Test data: { filePath = src\test\data\ManualTesting\SampleData.xml }<br><br>
Steps:
1. Click on command box
2. Type `open` `filePath`
3. Press enter on keyboard

Expected Result: 
1. Taskle switches to using SampleData.xml.
2. The new file path is reflected in the status bar footer.
3. Feedback message at the top that says file directory changed.

### 2. Adding a float task
Purpose: Test whether adding a task with just the name works.  
Prerequisite: Application must be started and running.  
Test data: { taskName = "Buy eggs" }<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName`
3. Press enter on keyboard

Expected Result: 
1. A new task called "Buy eggs" is added.
2. Task is labelled with the pending color code.
3. Feedback message at the top that says new task added.

### 3. Adding a deadline task
Purpose: Test whether adding a deadline with name and date works.  
Prerequisite: Application must be started and running.  
Test data: { taskName = "Submit project proposal", taskDate = "tmr" }<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` by `taskDate`
3. Press enter on keyboard

Expected Result: 
1. A new deadline called "Submit project proposal" is added.
2. The deadline is also added with the deadline date as the next day.
3. Event card is labelled with the pending color code.
4. Feedback message at the top that says new task added.


### 3. Adding an event task
Purpose: Test whether adding an event with name and dates works.  
Prerequisite: Application must be started and running.  
Test data: { taskName = "Class outing", startDate = "25 dec 10am", endDate = "6pm" }<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` from `startDate` to `endDate`
3. Press enter on keyboard

Expected Result: 
1. A new event called "Class outing" is added.
2. The event is also added with the date as "25 Dec, 10:00AM to 6:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top that says new task added.

### 4. Adding a task with reminder
Purpose: Test whether adding a task with name and reminder works.  
Prerequisite: Application must be started and running.  
Test data: { taskName = "Finish Assignment 4", remindDate = "11 Nov 2pm"}<br><br>
Steps:
1. Click on command box
2. Type `add` `taskName` remind `remindDate`
3. Press enter on keyboard
4. Feedback message at the top that says new task added.

Expected Result: 
1. A new task called "Finish Assignment 4" is added.
2. The reminder date is set as "11 Nov, 2:00PM".
3. Task is labelled with the pending color code.
4. Feedback message at the top that says new task added.

### 5. Removing a task
Purpose: Test whether removing a task works.  
Prerequisite: Application must be started and running.  
Test data: { taskIndex = "6" }<br><br>
Steps:
1. Click on command box
2. Type `remove` `taskIndex` 
3. Press enter on keyboard

Expected Result: 
1. Task number 6, xxxx is removed.
2. Feedback message at the top says removed tasks: 6.

### 6. Marking a task as Done
Purpose: Test whether done command works.  
Prerequisite: Application must be started and running.  
Test data: { taskIndex = "2" }<br><br>
Steps:
1. Click on command box
2. Type `done` `taskIndex` 
3. Press enter on keyboard

Expected Result: 
1. Task number 6, xxxx is removed from the current display.
2. Feedback message at the top says Task completed: 6.