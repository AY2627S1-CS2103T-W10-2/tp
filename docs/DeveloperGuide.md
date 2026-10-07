---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a Computer Science (CS) student at university
* actively builds and maintains academic and professional networks through modules, project teams, hackathons, tech communities, internships and other collaborative activities
* accumulates contacts across many overlapping academic and professional contexts
* often remembers the context in which they know someone, but not the person's exact details
* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Help CS students organise and retrieve people in their academic and professional network, especially when they remember the context in which they know someone but not the person's exact details. Each contact is saved with one or more contexts (e.g. a module, project team, hackathon or internship), so users can find the right person by name or context, faster than with a typical mouse-driven contact app.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

The stories below cover UniContacts' core requirements, future candidates, and ideas considered but excluded from the product scope. They describe user needs, not the implementation status of features. Future candidates are outside the MVP and are not commitments for the final release.

US1 to US22 retain the identifiers from the Project Notes.

#### Core requirements

| ID | Priority | As a ... | I want to ... | So that I can ... |
| --- | --- | --- | --- | --- |
| US1 | `* * *` | CS student who meets people at hackathons or datathons | record where and how I met someone when adding their contact | recall the connection even if I later forget their name |
| US4 | `* * *` | CS student with limited time | quickly add a contact with a name and context, even when I do not yet have their phone number or email | capture people I meet before I forget their details |
| US5 | `* * *` | CS student who meets people through different activities | organise contacts using contexts such as modules, clubs, internships, and events | remember which parts of my network each person belongs to |
| US10 | `* * *` | CS student who remembers an activity but not someone's name | search contacts by their associated module, event, project, or organisation | find the right person using the context I remember |
| US11 | `* * *` | CS student who knows someone through several activities | associate multiple contexts with the same contact | keep those connections together without creating a separate entry for each context |
| US15 | `* * *` | CS student who remembers only part of a name | search contacts using a full or partial name regardless of capitalisation | find someone without remembering their exact full name |
| US16 | `* * *` | CS student who knows people with similar names | see contextual information alongside search results | distinguish between the matching contacts |
| US18 | `* * *` | CS student reconnecting with people from a particular activity | view contacts associated with a module, event, project, or organisation | rediscover people from that group |

Context-based search (US10) is a core requirement because it directly supports UniContacts' value proposition: retrieving people through the contexts in which the user knows them.

#### Future candidates

These stories record useful extensions beyond the MVP. Their priorities express their relative importance; inclusion here does not mean they will all be implemented.

| ID | Priority | As a ... | I want to ... | So that I can ... |
| --- | --- | --- | --- | --- |
| US3 | `* *` | CS student looking for teammates or collaborators | filter contacts by skills, interests, or past project experience | find suitable people for future projects or hackathons |
| US6 | `* *` | CS student maintaining relationships | set reminders to check in with contacts | avoid losing touch with people in my network |
| US7 | `*` | CS student building a network | record and view which of my contacts know each other | understand the connections within my network |
| US8 | `* *` | CS student keeping track of friends' activities | record and view notes about a contact's current role, project, or location | recall what they are doing without asking for the same information again |
| US9 | `* *` | CS student exploring career paths | search or filter contacts by role, company, or field | identify people whose experience is relevant to my career interests |
| US12 | `* *` | CS student who connects with people through different channels | store additional contact channels, such as Telegram handles, alongside phone numbers and email addresses | choose an appropriate way to reach each person |
| US13 | `* *` | CS student whose project team or module has ended | archive contacts I no longer actively need | keep my active list relevant while retaining past connections |
| US14 | `* *` | CS student who regularly contacts a few key people | mark contacts as favourites | access them quickly without searching the whole list |
| US17 | `* *` | CS student who remembers several partial details about someone | narrow search results to contacts matching all the clues I provide | identify the person from the combination of details I remember |
| US19 | `* *` | CS student who met someone through another person | record who introduced me to a contact | remember how the connection was formed |
| US20 | `* *` | CS student whose contacts' details and shared activities change | edit contact details and add or remove contexts | keep records accurate without deleting and recreating contacts |
| US21 | `* *` | CS student who knows someone by more than one name | record and search a contact's nickname or alternative name | find them using the name I remember |
| US22 | `* *` | CS student who needs to remember additional details about a person | attach a short personal note to a contact | retain useful information that does not fit the standard contact fields |

The MVP already allows optional phone and email details; US12 extends this to additional channels. The MVP's multi-keyword search matches any supplied keyword, whereas US17 requires matching all supplied clues to narrow the results. Editing (US20) remains a future candidate as described in the MVP discussions.

#### Considered but excluded

The following story is retained to document the scope decision. UniContacts manages a student's personal contacts; social discovery is outside its intended scope. The priority records the desirability of the idea, not a commitment to implement it.

| ID | Priority | As a ... | I want to ... | So that I can ... |
| --- | --- | --- | --- | --- |
| US2 | `* *` | CS student seeking social connections | discover and start conversations with other students who share my interests | make friends beyond my existing contacts |

### Use cases

(For all use cases below, the **System** is `UniContacts` and the **Actor** is the `user`. Steps state the user's intention rather than command syntax; command formats and error messages are specified in the User Guide.)

**Use case: UC01 - Add a contact**

**Guarantees**

* If the entered data is invalid or duplicates an existing contact, no contact is added.
* Otherwise, the new contact is stored with its name and at least one context.

**MSS**

1.  User requests to add a contact, providing the contact's name, at least one context, and optionally a phone number and email
2.  System adds the contact and shows the details of the new contact

    Use case ends.

**Extensions**

* 1a. System detects an error in the entered data.

    * 1a1. System shows an error message with the correct format.

    * 1a2. User enters new data.

      Steps 1a1-1a2 are repeated until the data entered are correct.

      Use case resumes at step 2.

* 1b. The phone number or email matches an existing contact.

    * 1b1. System informs the user that the contact already exists.

      Use case ends.

* 2a. System is unable to save the change.

    * 2a1. System adds the contact for the current session and warns the user that the change may be lost when the app is closed.

      Use case ends.


**Use case: UC02 - Find contacts by context**

**Guarantees**

* No contact data is changed.
* Every saved contact with a context matching the request is shown, regardless of capitalisation.

**MSS**

1.  User requests to find contacts linked to a specific context
2.  System shows the matching contacts filtered by that specific context

    Use case ends.

**Extensions**

* 1a. User does not remember the exact context.

    * 1a1. User views all contexts (UC05).

      Use case resumes at step 1.

* 1b. User does not specify a context.

    * 1b1. System shows an error message with the correct format.

      Use case resumes at step 1.

* 2a. No contacts match the context.

    * 2a1. System shows an empty list and informs the user that no contacts were found.

      Use case ends.

**Use case: UC03 - Find contacts by name**

**Guarantees**

* No contact data is changed.
* Every saved contact whose name contains the requested name is shown, regardless of capitalisation.

**MSS**

1.  User requests to find contacts by a full or partial name
2.  System shows the matching contacts, each with its contexts

    Use case ends.

**Extensions**

* 1a. User does not specify a name.

    * 1a1. System shows an error message with the correct format.

      Use case resumes at step 1.

* 2a. No contacts match the name.

    * 2a1. System shows an empty list and informs the user that no contacts were found.

      Use case ends.

**Use case: UC04 - Delete a contact**

**Guarantees**

* If the user does not identify a contact in the displayed list, no contact is deleted.
* Only the identified contact is deleted; other contacts, including ones with the same name, are unchanged.
* The deleted contact no longer appears in the contact list.

**MSS**

1.  User requests to list contacts
2.  System shows a list of contacts
3.  User requests to delete a specific contact in the list
4.  System deletes the contact and shows the details of the deleted contact

    Use case ends.

**Extensions**

* 1a. User finds the contact by context (UC02) or by name (UC03) instead.

  Use case resumes at step 3.

* 2a. The list is empty.

  Use case ends.

* 3a. The requested contact is not in the displayed list.

    * 3a1. System shows an error message.

      Use case resumes at step 2.

* 4a. System is unable to save the change.

    * 4a1. System deletes the contact for the current session and warns the user that the change may be lost when the app is closed.

      Use case ends.

**Use case: UC05 - View all contexts**

**Guarantees**

* No contact data is changed, and the displayed contact list is unchanged.
* Each context appears once, regardless of capitalisation or extra spaces.

**MSS**

1.  User requests to view all contexts
2.  System shows each context in alphabetical order, with the number of contacts linked to it

    Use case ends.

**Extensions**

* 2a. No contacts have been saved.

    * 2a1. System informs the user that there are no contexts yet.

      Use case ends.



### Non-Functional Requirements

1.  UniContacts should work on Windows, Linux, and macOS computers with Java `25` installed.
2.  UniContacts should be distributed as a single JAR file no larger than 100 MB and should not require an installer.
3.  All features should remain usable without an Internet connection, a user account, or a remote server.
4.  Contact data should be stored locally in a human-editable text file without using a database management system. Correctly formatted manual edits to the data file should be loaded by the application.
5.  UniContacts should support a single user per installation and should not require its data file to be shared or accessed concurrently.
6.  Contact data should not be transmitted outside the user's computer.
7.  UniContacts should be able to hold up to 1000 contacts. Commands used during typical operation, such as adding, editing, deleting, listing, and finding contacts, should complete within one second.
8.  A user with above-average typing speed for regular English text should be able to perform all primary contact-management tasks using only the keyboard and faster than with an equivalent mouse-driven interface.
9.  The GUI should work well at resolutions of `1920x1080` and above with screen scales of 100% and 125%. It should remain usable at resolutions of `1280x720` and above with a screen scale of 150%.
10. Invalid commands and invalid contact details entered by the user should not modify existing contact data or cause the application to stop responding. The application should display an error message and remain usable.

### Glossary

* **Hackathon**: A collaborative event for programmers to work intensively to build functional software or hardware prototypes.
* **Datathon**: A collaborative competition—similar to a hackathon—where participants analyze complex, real-world datasets to build data-driven applications, machine learning models, or strategic insights within a set timeframe
* **Duplicate**: The input is considered as a duplicate when the email/phone number inputted matches one of stored emails/phone numbers in the database. Inputs with same name matched is allowed and will not be considered as a duplicate.
* **Capitalisation**: Whether the letters in a context name are uppercase or lowercase.
* **Contact**: A saved record representing a specific person within the user's academic, professional, or social network.
* **Context**: A descriptive tag representing the shared environment, activity, or relationship (such as a school module, project team, CCA, internship, or event) that explains how or where the user knows a specific person.
* **Index**: A numerical value used to identify and select a specific contact based on its position in the currently displayed contact list.
* **Partial-match**: A name/context input can match to multiple contacts in the local JSON.
* **Case-insensitive**: No distinctions between uppercase and lowercase inputs including commands, names and contexts.
* **Session**: A single continuous period of using UniContacts, starting when the application is opened and ending when it is closed.

*{More to be added}*

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
