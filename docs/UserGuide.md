# User Guide

* [About](#about)
* [Quick Start](#quick-start)
* [Features](#features)
* [FAQ](#faq)
* [Command Summary](#command-summary)

<!-- @@author A0125509H -->
## About
Do you have so many tasks to do every day that you simply cannot keep track of them anymore? Don't you wish that there was an easier way to stay on top of your daily tasks without feeling stressed out?

Taskle is here to help you with all of that! It is a task management application with a single text box for all your commands. Coupled with its simplicity, managing your tasks has never been this easy!

Reorganize your life now by using Taskle!

<br>

<!-- @@author A0139402M -->
## Quick Start

0. Ensure that you have Java version `1.8.0_60` or later installed in your computer.<br>
   > Having any Java 8 version is not enough. <br>
   This application will not work with earlier versions of Java 8.
   
1. Download the latest `Taskle.jar` from the [releases](../../../releases) tab.

2. Copy the file to the folder where you want to use Taskle.

3. Double-click on the file to start the application. The Graphical User Interface (GUI) should appear in a few seconds.
	
	> <img src="images/UI/Main.png" align="center">
	<div align="center">Figure 1: GUI of Taskle</div>

4. Type the command in the command box and press <kbd>Enter</kbd> to execute it.
	> Example: Typing **`help`** and pressing <kbd>Enter</kbd> will open up the help window.
 
5. Some commands that you can try:
   * `add` **`Buy Milk`** : Adds a "Buy Milk" task into Taskle.
   * `add` **`Submit Proposal by 7 Nov`** : Adds a "Submit Proposal" task which is to be completed by 7 Nov.
   * `remove` **`5`** : Removes the task with index 5 from the current list.
   * `clear`: Clears all tasks from Taskle.
   * `exit` : Exits from Taskle.
   
6. Refer to the [Features](#features) section for details of each command.

<br>

<!-- @@author A0141780J -->
## Features

Taskle makes managing your tasks both simple and elegant as all of its features can be accessed with a single line of command!

> **Note that the following conventions are used for all command formats:**
> * **`BOLDED`** words are parameters.
> * Items in `[SQUARE_BRACKETS]` are optional.
> * The order of parameters is fixed.

<br>

### Add a Task / Event: `add`
   * Adds a task / event into Taskle (with its respective deadline or end-date.)
   * A reminder time can be set for tasks / events.
   * The time parameter is optional.<br><br>

| Format  
| :-------- 
| `add` **`task_name`** `remind` [**`date time`**] | 
| `add` **`task_name`** `by` [**`date time`**] `remind` [**`date time`**] |
| `add` **`task_name`** `from` [**`date time`**] `to` [**`date time`**] `remind` [**`date time`**]

> **Things to Note**
> * You may enter the date and time in any format that you desire, and Taskle will still be able to recognise it.
>	* Example: "add Do Homework by **tmr**" adds a task with the name "Do Homework" with the deadline to be set to tomorrow (tmr is recognised as tomorrow.)
> * Only the words after the last **by** or **from** in your input will be accepted as the date and time. This allows you to continue using **by** and **from** as part of your task name as long as you enter the date and times last.
> 	* Example: "add Gardens by the Bay **from** 9am to 9pm today" adds a task with the name "Gardens by the Bay" with the date and time to be set to "today, 9am to 9pm".
> * Should the entered name be too long, it will appear truncated with ellipses on the application. However, you can still view the full name by clicking and holding down your mouse button. 

Examples:
* `add` **`Pay Phone Bills`**
* `add` **`Do CS2101 Assignment`** `by` **`12 Oct`**
* `add` **`Business Trip`** `from` **`4 Oct`** `to` **`5 Oct`** `remind` **`3 Oct 2pm`**

<img align="center" src="images/UI/Add.png" >
<div align="center">Figure 2: Adds a task with the name "Pay Phone Bills" into Taskle</div><br>
	
<br>

<!-- @@author A0140047U -->
### Edit a Task: `rename; reschedule; remind`
Edits an existing task in Taskle. There are three types of possible edits: Rename, Reschedule and Remind. You are required to input the **task_number** (as indicated in Figure 3) in order for Taskle to identify the task that you wish to edit.<br><br>

Type | Format  
:-------- | :-------- 
Rename | `rename` **`task_number`** **`new_task_name`**  
Reschedule | `reschedule` **`task_number`** [**`date time`**] `to` [**`date time`**] `remind` [**`date time`**]
Remind | `remind` **`task_number`** [**`date time`**]

> **Things to Note**
> * A  task can only have **one reminder**. The date and time that were specified in the "remind" command will replace any existing reminder.
> * If **no time** was specified for the reminder, Taskle will automatically assign 00:00 of the specified reminder date to it.
> * To remove a reminder or deadline from a task, you will have to type **"clear"** after typing the task number. For example:
>	* remind **2 clear**
>	* reschedule **2 clear**

Examples:
* `rename` **`8 Pay Abel for Chicken Rice`**

<img align="center" src="images/UI/Rename_Before.png">
<div align="center">Figure 3: Renames Task 8 to "Pay Abel for Chicken Rice" (Before)</div><br>

<img align="center" src="images/UI/Rename_After.png">
<div align="center">Figure 4: Renames Task 8 to "Pay Abel for Chicken Rice" (After)</div><br>

* `reschedule` **`2`** `to` **`9th November 2pm`**

<img align="center" src="images/UI/Reschedule_Before.png">
<div align="center">Figure 5: Reschedules Task 2 to "9th November 2pm" (Before)</div><br>

<img align="center" src="images/UI/Reschedule_After.png">
<div align="center">Figure 6: Reschedules Task 2 to "9th November 2pm" (After)</div><br>

* `remind` **`2 8th November 7pm`**
	
<img src="images/UI/Remind_Before.png" align="center">
<div align="center">Figure 7: Sets a Reminder for Task 2 on "8th November 7pm" (Before)</div><br>

<img src="images/UI/Remind_After.png" align="center">	
<div align="center">Figure 8: Sets a Reminder for Task 2 on "8th November 7pm" (After)</div><br>

<br>

<!-- @@author A0125509H -->
### Remove a Task: `remove`
Removes a task from Taskle permanently.<br><br>

| Format  
| :-------- 
| `remove` **`task_number`** | 

Example:
* `remove` **`8`**

<img src="images/UI/Remove.png" align="center"/>	
<div align="center">Figure 9: Removes Task 8 from Taskle</div><br>

<br>

<!-- @@author A0139402M -->
### Find Tasks : `find`
Finds all the tasks in Taskle that match the list of keywords and specified statuses.<br><br>

| Format  
| :-------- 
| `find` **`keywords [-status]`** | 

> **Things to Note**
> * The following statuses are available:
>	* **-all**: All the tasks in Taskle.
>	* **-pending**: Pending tasks.
>	* **-done**: Tasks that have been marked as done.
>	* **-overdue**: Tasks that are overdue (only applicable to deadlines.)
> * All the tasks that match the list of keywords and specified statuses will be displayed.
> * You must specify **at least one keyword** when using find.
> * It is optional to specify the status. If no status was specified, the default tasks (pending and overdue) will be displayed. 
> * You may specify more than one status. All tasks that fulfill any of these statuses will be displayed. 
>	* Example:  "`find` `meeting` `task` **`-pending`** **`-overdue`**"

Example:
* `find` **`project`**

<img src="images/UI/Find.png" align="center">
<div align="center">Figure 10: Finds "pending and overdue" tasks that have "project" in their names</div><br>

* `find` **`project`** **`-all`**	
	
<img src="images/UI/Find_All.png" align="center">
<div align="center">Figure 11: Finds all tasks that have "project" in their names</div><br>

<br>

<!-- @@author A0140047U -->
### List Tasks: `list`
Lists tasks according to specified statuses (done, overdue, or pending.)<br><br>

| Format  
| :-------- 
| `list` **`[-status]`**| 

> **Things to Note**
> * The following statuses are available:
>	* **-all**: All the tasks in Taskle.
>	* **-pending**: Pending tasks.
>	* **-done**: Tasks that have been marked as done.
>	* **-overdue**: Tasks that are overdue (only applicable to deadlines.)
> * It is optional to specify the status. If no status was specified, the default tasks (pending and overdue) will be displayed. 
> * You may specify more than one status. All tasks that fulfill any of these statuses will be displayed. 
>	* Example:  "`list` **`-pending`** **`-overdue`**"

Example:
* `list` **`-all`**

<br>

<!-- @@author A0125509H -->
### Mark a Task as Done: `done`
Marks a task as done (when it is completed.)<br><br>

| Format  
| :-------- 
| `done` **`task_number`** | 

Example:
* `done` **`5`**

<img src="images/UI/Done.png" align="center">
<div align="center">Figure 12: Marks Task 5 as done</div><br>

<br>

<!-- @@author A0140047U -->
### Undo a Recent Command: `undo`
Undoes a previous command. It can be called multiple times to return Taskle to a previous state.<br><br>

| Format  
| :-------- 
| `undo` | 

<br>

### Redo a Undo: `redo`
Redoes a previous command. This command is used when you wish to revert a wrongly issued undo command.<br><br>

| Format  
| :-------- 
| `redo` | 

<br>

<!-- @@author A0139402M -->
### Clear all Tasks: `clear`
Clears all tasks from Taskle.<br><br>

| Format  
| :-------- 
| `clear` | 

<br>

<!-- @@author A0140047U -->
### Change Save Directory: `save`
Changes the save directory of Taskle's data file.<br><br>

| Format  
| :-------- 
| `save` **`directory_path`**|

<br>

> **Thing to Note**
> * You are also able to change your save directory from the menu option.

Example:
* `save` **`C:\Users\W14-C3\Dropbox`**

<img src="images/UI/Save.png" align="center">
<div align="center">Figure 13: Changes the Save Directory of Taskle's data file</div><br>

<br>
 
### Open Data File: `open`
Opens a Taskle data file.<br><br>

| Format  
| :-------- 
| `open` **`file_path`**|

> **Thing to Note**
> * You are also able to open your file from the menu option.

Example:
* `open` **`C:\Users\W14-C3\Dropbox\Taskle.xml`**

<img src="images/UI/Open.png" align="center">
<div align="center">Figure 14: Opens a Taskle data file</div><br>

<br>

<!-- @@author A0139402M -->
### View Help: `help`
Displays a list of available commands.<br>

| Format  
| :-------- 
| `help` | 

> **Things to Note**
> * Help is also shown if you enter an incorrect command.
>	* Example: "abcd"
> * A list of available commands is shown in a separate window so that you can refer to it while using Taskle.

<img src="images/UI/Help.png" align="center">
<div align="center">Figure 15: Help Window</div><br>

<br>

### Exit Taskle: `exit`
Exits and closes Taskle.<br><br>

| Format  
| :-------- 
| `exit` |
 
<br>

<!-- @@author A0141780J -->
## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: You can install Taskle in the other computer and overwrite its empty data file with the file that was created in your previous Taskle folder. You can also use Taskle's save and open features to transfer your data.

<br>
       
## Command Summary

The table below shows the overall list of commands used in the application.

> **Note that the following conventions are used for all command formats:**
> * **`BOLDED`** words are parameters.
> * Items in `[SQUARE_BRACKETS]` are optional.
> * The order of parameters is fixed.

<br>

Command `(Shortcut)` | Format  
:-------- | :-------- 
Add `a` | `add` **`task_name`** `remind` [**`date time`**]
 		| `add` **`task_name`** `by` [**`date time`**] `remind` [**`date time`**] 
 		| `add` **`task_name`** `from` [**`date time`**] `to` [**`date time`**] `remind` [**`date time`**]
Rename `rn` | `rename` **`task_number`** **`new_task_name`** 
Reschedule `rs` | `reschedule` **`task_number`** [**`date time`**] `to` [**`date time`**] `remind` [**`date time`**]
Remind `rmd` | `remind` **`task_number`** [**`date time`**]
Remove `rm` | `remove` **`task_number`**
Find `f` | `find` **`keywords [-status]`**
List `l` | `list` **`[-status]`**
Mark as Done `d` | `done` **`task_number`**
Undo `u` | `undo`
Redo `r` | `redo`
Clear | `clear`
Change Directory `s` | `save`
Open Data File `o` | `open`
View Help `h` | `help`
Exit | `exit`