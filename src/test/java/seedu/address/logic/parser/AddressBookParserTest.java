package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ALIAS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTRODUCER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ContextsCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SetArchivedByContextCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.commands.ViewArchivesCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Context;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesCluesPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);

        AddCommand commandUpperCase = (AddCommand) parser.parseCommand(
                AddCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " " + PersonUtil.getPersonDetails(person));
        assertEquals(new AddCommand(person), commandUpperCase);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);

        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD.toUpperCase(Locale.ROOT)) instanceof ClearCommand);
        assertTrue(parser.parseCommand(
                ClearCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_contexts_ignoresExtraTextAndCommandCase() throws Exception {
        for (String input : List.of("contexts", "contexts CS2103T", "CONTEXTS", "CoNtExTs ignored", "  contexts  ")) {
            assertTrue(parser.parseCommand(input) instanceof ContextsCommand);
        }
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);

        DeleteCommand commandUpperCase = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), commandUpperCase);
    }

    @Test
    public void parseCommand_deleteWithWhitespace_success() throws Exception {
        for (String input : List.of("  delete   1  ", "\tdelete\t1\t")) {
            assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().withAlias(VALID_ALIAS_AMY).withIntroducer(VALID_INTRODUCER_AMY)
                .withNote(VALID_NOTE_AMY).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);

        EditCommand commandUpperCase = (EditCommand) parser.parseCommand(
                EditCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " " + INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), commandUpperCase);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);

        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD.toUpperCase(Locale.ROOT)) instanceof ExitCommand);
        assertTrue(parser.parseCommand(
                ExitCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new PersonContainsKeywordsPredicate(keywords)), command);

        FindCommand commandUpperCase = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " "
                + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new PersonContainsKeywordsPredicate(keywords)), commandUpperCase);
    }

    @Test
    public void parseCommand_findWithStructuredClues() throws Exception {
        FindCommand expectedCommand = new FindCommand(new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("CS2103T")));

        assertEquals(expectedCommand, parser.parseCommand("find n/Tony c/CS2103T"));
        assertEquals(expectedCommand, parser.parseCommand("find c/CS2103T n/Tony"));
        assertEquals(expectedCommand, parser.parseCommand("FIND n/Tony c/CS2103T"));
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);

        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD.toUpperCase(Locale.ROOT)) instanceof HelpCommand);
        assertTrue(parser.parseCommand(
                HelpCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);

        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD.toUpperCase(Locale.ROOT)) instanceof ListCommand);
        assertTrue(parser.parseCommand(
                ListCommand.COMMAND_WORD.toUpperCase(Locale.ROOT) + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_lineSeparatorInArguments_reportsFieldError() {
        // a line separator in the note must give the note error, not the generic "Invalid command format!"
        assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> parser.parseCommand(
                AddCommand.COMMAND_WORD + " n/Amy Bee c/CS2103T nt/a\u2028b"));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("UNKNOWNCOMMAND"));
    }

    @Test
    public void parseCommand_mixedCaseCommandWord_success() throws Exception {
        assertTrue(parser.parseCommand("LiSt") instanceof ListCommand);
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON),
                parser.parseCommand("DeLeTe " + INDEX_FIRST_PERSON.getOneBased()));
        assertTrue(parser.parseCommand("FiNd Alex") instanceof FindCommand);
    }

    @Test
    public void parseCommand_archiveCommands() throws Exception {
        assertTrue(parser.parseCommand(ArchiveCommand.COMMAND_WORD + " 1") instanceof ArchiveCommand);
        assertTrue(parser.parseCommand("ARCHIVE 1") instanceof ArchiveCommand);
        assertTrue(parser.parseCommand(UnarchiveCommand.COMMAND_WORD + " 1") instanceof UnarchiveCommand);
        assertTrue(parser.parseCommand("Unarchive 1") instanceof UnarchiveCommand);
        assertTrue(parser.parseCommand(ViewArchivesCommand.COMMAND_WORD) instanceof ViewArchivesCommand);
        assertTrue(parser.parseCommand("ARCHIVES") instanceof ViewArchivesCommand);
        assertEquals(new SetArchivedByContextCommand(new Context("CS2103T"), true),
                parser.parseCommand("archive c/CS2103T"));
        assertEquals(new SetArchivedByContextCommand(new Context("CS2103T"), false),
                parser.parseCommand("unarchive c/CS2103T"));
        // unlike list, archives does not ignore text after it, as it is one letter away from archive
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewArchivesCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand("archives 1"));
    }
}
