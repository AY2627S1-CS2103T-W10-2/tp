---
layout: page
title: User Guide
---

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all persons.

   * `add n/John Doe c/CS2103T p/98765432 e/johnd@example.com` : Adds a person named `John Doe` to the address book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user. Some parameters come after a prefix that says what they are, such as `n/` for a name. Others, such as `INDEX` in `delete INDEX`, have no prefix.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [p/PHONE]` can be used as `n/John Doe p/98765432` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[c/CONTEXT]…​` may be omitted, or written as `c/CS2103T` or `c/CS2103T c/NUS Hackers`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Command words are case-insensitive, but prefixes must be in lowercase.<br>
  For example, `add`, `ADD` and `Add` all work, but `N/John Doe` is not read as `n/John Doe`.

* Commands that take no parameters, such as `help`, `list`, `exit` and `clear`, ignore anything typed after them.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Every person has at least one context: how you know them, such as a module, project, CCA, internship or event.

Format: `add n/NAME c/CONTEXT [c/CONTEXT]…​ [p/PHONE] [e/EMAIL]`

* `n/NAME` and at least one `c/CONTEXT` are compulsory. `p/PHONE` and `e/EMAIL` are optional.
* To give more than one context, repeat `c/`, one context each. `n/`, `p/` and `e/` can each be given only once.
* Spaces before and after each value are ignored.

Examples:
* `add n/Alex Tan c/CS2103T` adds `Alex Tan` with just a name and one context.
* `add n/Priya Nair c/Hack&Roll 2026 c/NUS Hackers p/91234567 e/priya@u.nus.edu` adds `Priya Nair` with two contexts, a phone number and an email.
* `add n/Jean-Luc O'Brien c/Google internship e/jl.obrien@gmail.com` adds `Jean-Luc O'Brien` with an email but no phone number.
* `add e/mei@u.nus.edu c/CS2101 n/Lim Mei` adds `Lim Mei`, showing that parameters can be in any order.

**Parameter rules**

Parameter | Rules | Valid | Invalid
----------|-------|-------|--------
`n/NAME` | Starts with a letter. Only English letters, spaces, hyphens `-`, apostrophes `'` and periods `.`. At most 100 characters. | `Alex Tan`, `Jean-Luc O'Brien`, `Lim J.H.` | `Alex123`, `Raj s/o Kumar`, `@alex`
`c/CONTEXT` | Starts with a letter or digit. Only English letters, digits, spaces and `&` `-` `.` `'` `#`. At most 50 characters. | `CS2103T`, `Hack&Roll 2026`, `Tut #3` | `CS2103T/T09`, `(orbital)`
`p/PHONE` | An optional `+`, then 3 to 15 digits. No spaces, hyphens or brackets. | `91234567`, `+6591234567` | `9123-4567`, `+65 9123 4567`, `12`
`e/EMAIL` | Of the form `local-part@domain`, at most 254 characters. See the email rules below. | `e0123456@u.nus.edu`, `alex.tan+work@gmail.com` | `alex@`, `.alex@gmail.com`, `alex..tan@gmail.com`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
`/` is not allowed in names, so enter a name such as `Raj s/o Kumar` as `Raj s o Kumar` or `Raj son of Kumar`.
</div>

Email rules:
* The local part (before the `@`) may contain letters, digits and `+` `_` `.` `-`, but cannot start or end with a symbol, or have two symbols in a row.
* The domain (after the `@`) is one or more parts separated by periods, such as `u.nus.edu`. Each part may contain letters, digits and hyphens, but cannot start or end with a hyphen. The last part must be at least 2 characters long.

How values are saved:
* Names, contexts and emails keep the capitalisation you typed.
* Several spaces in a row inside a context are saved as one space. For example, if you type `NUS`, three spaces, then `Hackers`, the context is saved as `NUS Hackers`. The 50-character limit is checked after this, so extra spaces never make a context too long.
* Names are saved exactly as typed, including several spaces in a row.
* Contexts that differ only in capitalisation or repeated spaces count as the same context. If you give the same context twice, such as `c/CS2103T c/cs2103t`, the person gets it once, spelt as you first typed it.

**If the command succeeds**

The list shows all persons again, with the new person at the bottom. The result display shows the new person's details. For example, adding Priya Nair (the second example above) shows:

`New person added: Priya Nair; Contexts: [Hack&Roll 2026] [NUS Hackers]; Phone: 91234567; Email: priya@u.nus.edu`

Contexts are listed in alphabetical order, ignoring capitalisation. A missing phone number or email is left out.

**Duplicate persons**

A person is a duplicate if they have the same phone number or the same email as an existing person. Emails are compared ignoring capitalisation, so `Alex@U.NUS.EDU` and `alex@u.nus.edu` count as the same email. Duplicates are not added. For example, with the sample data, `add n/Alexander Yeoh c/CS2101 p/87438807` shows:

`This person already exists: Alex Yeoh has the same phone number (87438807).`

* Two persons can have the same name, as long as their phone numbers and emails are different.
* A person with no phone number and no email is never a duplicate.
* `91234567` and `+6591234567` count as different phone numbers.

**If the command fails**

Nothing is added, and your command stays in the command box so that you can fix it. If there are several problems, only the first is shown.

Problem | Example | Message shown
--------|---------|--------------
`n/` or `c/` missing | `add n/Alex Tan` | `Invalid command format!`, followed by how to use `add`
Text before the first prefix, or an uppercase prefix | `add Alex n/Alex Tan c/CS2103T`<br>`add N/Alex Tan c/CS2103T` | `Invalid command format!`, followed by how to use `add`
`n/`, `p/` or `e/` given more than once | `add n/Alex n/Alex Tan c/CS2103T` | `Multiple values specified for the following single-valued field(s): n/`
Nothing after `n/`, `c/`, `p/` or `e/` | `add n/Alex Tan c/` | Depending on the prefix:<br>`Name cannot be empty.`<br>`Contexts cannot be empty.`<br>`Phone number cannot be empty. Leave out p/ if there is no phone number.`<br>`Email cannot be empty. Leave out e/ if there is no email.`
A value that breaks its rule | `add n/Alex Tan c/CS2103T p/9123-4567` | That parameter's rule, such as `Phone numbers should contain only digits, may start with +, and have 3 to 15 digits.`
A prefix that `add` does not use, such as `a/` or `t/` | `add n/Alex Tan a/Blk 123 c/CS2103T` | The prefix and its text become part of the value before it. Here the name becomes `Alex Tan a/Blk 123`, so the name rule is shown.

### Listing all persons: `list`

Displays all persons saved.

If the displayed person list was previously filtered using another command, such as `find`, the `list` command restores the complete person list.

Format: `list`

Each person is displayed in the following order:

1. Name
2. Contexts
3. Phone number, if provided
4. Email address, if provided

Contexts are displayed in alphabetical order, ignoring capitalisation. Missing phone numbers and email addresses are not displayed.

After the command is executed, the result display shows the number of persons listed:

* `1 person listed.` when exactly one person is listed.
* `NUMBER people listed.` when zero or more than one person is listed.

Examples:

* `list` — Displays all saved persons.
* `LIST` — Command words are case-insensitive, so this also displays all persons.
* If `find Tony` currently shows only persons matching `Tony`, entering `list` restores the complete person list.

<div markdown="block" class="alert alert-info">

**Note:**

* The `list` command does not add, remove, edit, or reorder persons.
* Person index numbers refer to the currently displayed list. After restoring the complete list, check the displayed index before using commands such as `edit` or `delete`.
* If there are no persons, the displayed list remains empty and the result display shows `0 people listed.`

</div>

### Editing a person: `edit`

Edits the person at the given index in the displayed list.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [-p/] [-e/] [+c/CONTEXT]…​ [-c/CONTEXT]…​`

* `INDEX` is the number shown beside the person in the displayed list, such as `1` or `2`. After a `find`, it refers to the filtered list.
* At least one of the other parameters must be given. Anything you leave out stays as it is.
* `n/NAME`, `p/PHONE` and `e/EMAIL` replace the current value. Their values follow the same rules as in [`add`](#adding-a-person-add).
* `-p/` removes the phone number and `-e/` removes the email. They take no value after them. Removing a phone number or email the person does not have changes nothing.
* `+c/CONTEXT` adds a context and `-c/CONTEXT` removes one. Repeat them to add or remove several contexts, one context each.
* `-c/` is applied before `+c/`, so you can swap a person's only context in one command.
* A person must keep at least one context.
* `n/`, `p/`, `e/`, `-p/` and `-e/` can each be given only once.

Examples:
* `edit 1 p/91234567 e/alex.yeoh@u.nus.edu` changes the 1st person's phone number and email.
* `edit 2 +c/NUS Hackers -c/Hiking club` adds `NUS Hackers` to the 2nd person and removes `Hiking club`.
* `edit 3 +c/RC4 -c/Hall` replaces the 3rd person's only context, `Hall`, with `RC4`.
* `edit 2 -c/hiking club +c/Hiking Club` changes how the 2nd person's `Hiking club` context is spelt.
* `edit 4 n/David Li Wei -e/` renames the 4th person and removes their email.

How contexts are matched:
* Contexts are compared ignoring capitalisation and repeated spaces, as in `add`. So `-c/hiking club` removes `Hiking club`.
* `+c/` with a context the person already has changes nothing, and the existing spelling is kept. To change the spelling, remove the context and add it back, as in the 4th example.
* If any `-c/` context is not one the person has, nothing is changed.

**If the command succeeds**

The list shows all persons again. The result display shows the person's new details. For example, with the sample data, `edit 2 +c/NUS Hackers -c/Hiking club` shows:

`Edited person: Bernice Yu; Contexts: [CS2103T] [NUS Hackers]; Phone: 99272758; Email: berniceyu@example.com`

**Duplicate persons**

Edited persons are checked for duplicates the same way as in [`add`](#adding-a-person-add): the edit is rejected if another person has the same phone number or email. A person is never a duplicate of themselves, so `edit 1 p/87438807` on Alex Yeoh, who already has that number, is accepted.

**If the command fails**

Nothing is changed, and your command stays in the command box so that you can fix it. If there are several problems, only the first is shown.

Problem | Example | Message shown
--------|---------|--------------
No parameter after the index | `edit 1` | `At least one field to edit must be provided.`, followed by how to use `edit`
Index missing, zero, negative or not a number | `edit 0 n/Alex Tan` | `Invalid command format!`, followed by how to use `edit`
Index larger than the displayed list | `edit 99 n/Alex Tan` | `The person index provided is invalid.`
`c/` instead of `+c/` or `-c/` | `edit 1 c/CS2040S` | `Use +c/ to add a context or -c/ to remove one.`
A `-c/` context the person does not have | `edit 1 -c/CS2040S` | `Alex Yeoh does not have context(s): [CS2040S]. Check the spelling.`
Removing every context | `edit 1 -c/CS2103T` | `A person must have at least one context. Add a new context with +c/ before removing the last one.`
`p/` and `-p/`, or `e/` and `-e/`, together | `edit 1 p/91234567 -p/` | `Cannot set and remove the phone number in the same command.`<br>`Cannot set and remove the email in the same command.`
A value after `-p/` or `-e/` | `edit 1 -p/91234567` | `-p/ does not take a value. Use p/PHONE to change the phone number.`<br>`-e/ does not take a value. Use e/EMAIL to change the email.`
Nothing after `n/`, `p/`, `e/`, `+c/` or `-c/` | `edit 1 p/` | Depending on the prefix:<br>`Name cannot be empty.`<br>`Phone number cannot be empty. Use -p/ to remove the phone number.`<br>`Email cannot be empty. Use -e/ to remove the email.`<br>`Contexts cannot be empty.`
`n/`, `p/`, `e/`, `-p/` or `-e/` given more than once | `edit 1 p/91234567 p/98765432` | `Multiple values specified for the following single-valued field(s): p/`
A value that breaks its rule | `edit 1 p/9123-4567` | That parameter's rule, such as `Phone numbers should contain only digits, may start with +, and have 3 to 15 digits.`
Another person has the same phone number or email | `edit 1 p/99272758` | `This person already exists: Bernice Yu has the same phone number (99272758).`

### Finding persons by name or context: `find`

Finds persons whose names or contexts contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]…`

* The command word and search are case-insensitive; for example, `FIND cs2103t` works.
* Partial matches are supported; for example, `Alex` matches both `Alex Tan` and `Alexander Lim`.
* A keyword can match either a person's name or any of their contexts.
* Persons matching at least one keyword are returned (an `OR` search).
* Each `find` command searches the full person list, even if a previous command filtered the displayed results.
* Extra spaces before, between, or after keywords are ignored.
* If no persons match, the app shows `No persons found!`.

Examples:
* `find Alex` returns persons such as `Alex Tan` and `Alexander Lim`.
* `find CS2103T` returns persons with `CS2103T` as a context.
* `find Alex CS2103T` returns persons matching either `Alex` or `CS2103T`.

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

Each save is written to a temporary file first and then moved over `data/addressbook.json`. If AddressBook is closed or crashes in the middle of a save, your previous data file is left as it was instead of being half-written.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Each person is stored like this:

```json
{
  "persons" : [ {
    "name" : "Alex Yeoh",
    "phone" : "87438807",
    "email" : "alexyeoh@example.com",
    "contexts" : [ "CS2103T", "Hiking club" ]
  }, {
    "name" : "Roy Balakrishnan",
    "contexts" : [ "Internship" ]
  } ]
}
```

* `name` and at least one entry in `contexts` are required.
* `phone` and `email` are optional. You can leave them out, as for Roy above, or set them to `null`.
* Each context is a plain piece of text, such as `"CS2103T"`, and must follow the same rules as a context entered in the `add` command.
* Two persons cannot have the same phone number or the same email.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Edit the data file only while AddressBook is closed, and only if you are confident that you can update it correctly. Certain edits can cause AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range).
</div>

#### If the data file cannot be read

If AddressBook cannot read the data file when it starts (for example, a person has no context or has an invalid phone number or email, two persons share a phone number or email, the file is not valid JSON, or the file was saved by an older version that used addresses and tags), it:

1. renames the file to `addressbook.json.<UTC time>.bak` in the same `data` folder, for example `addressbook.json.20261015T101723Z.bak`, so that your data is not overwritten; and
2. starts with an empty address book. A new `data/addressbook.json` is created when you run your first command.

To recover your data:

1. Exit AddressBook.
2. Open the `.bak` file in a text editor and fix the problem. The reason is written in the log file `addressbook.log`, which is in the folder you started AddressBook from.
3. If there is a new `addressbook.json` (it exists if you ran any command after the failed start), rename it to `addressbook-new.json` so that it is not overwritten. Then rename the fixed `.bak` file to `addressbook.json`.
4. Start AddressBook again. Any contacts you added after the failed start are still in `addressbook-new.json`; add them again if you need them.

If AddressBook fails to read the data file more than once, each failure keeps its own `.bak` file, because the time is part of the name.

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

**Q**: AddressBook started with an empty list, but I had saved contacts. What happened?<br>
**A**: AddressBook could not read your data file, so it kept the file as `data/addressbook.json.<UTC time>.bak` and started empty. Your data is in that `.bak` file. See [If the data file cannot be read](#if-the-data-file-cannot-be-read) to recover it.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME c/CONTEXT [c/CONTEXT]…​ [p/PHONE] [e/EMAIL]` <br> e.g., `add n/James Ho c/CS2103T c/NUS Hackers p/22224444 e/jamesho@example.com`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [-p/] [-e/] [+c/CONTEXT]…​ [-c/CONTEXT]…​`<br> e.g., `edit 2 p/91234567 +c/NUS Hackers -c/Hiking club`
**Find** | `find KEYWORD [MORE_KEYWORDS]…`<br> e.g., `find Alex CS2103T`
**List** | `list`
**Help** | `help`
