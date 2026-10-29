package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_ALIAS;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_CONTEXT;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_EMAIL;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_INTRODUCER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_NAME;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_NOTE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_PHONE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_CONTEXT = "(orbital)";
    private static final String INVALID_ALIAS = "Alex123";
    private static final String INVALID_INTRODUCER = "Ben123";
    private static final String INVALID_NOTE = "x".repeat(301);

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_CONTEXT_1 = "CS2103T";
    private static final String VALID_CONTEXT_2 = "Hiking club";
    private static final String VALID_ALIAS = "Alex";
    private static final String VALID_INTRODUCER = "Ben Lee";
    private static final String VALID_NOTE = "Met at Hack&Roll";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        for (String input : List.of("", " ", "0", "-1", "1.5", "Alex", "+1", "#", "1 2", "1 abc")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndex(input));
        }
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        for (String input : List.of(Long.toString((long) Integer.MAX_VALUE + 1), "999999999999999999999")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndex(input));
        }
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex(WHITESPACE + "1" + WHITESPACE));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_NAME, () -> ParserUtil.parseName(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_NAME, () -> ParserUtil.parseName(WHITESPACE));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_repeatedSpaces_keepsSpacesAsTyped() throws Exception {
        assertEquals("John   Doe", ParserUtil.parseName("John   Doe").fullName);
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_PHONE, () -> ParserUtil.parsePhone(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_PHONE, () -> ParserUtil.parsePhone(WHITESPACE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_EMAIL, () -> ParserUtil.parseEmail(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_EMAIL, () -> ParserUtil.parseEmail(WHITESPACE));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseAlias_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAlias(null));
    }

    @Test
    public void parseAlias_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Alias.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseAlias(INVALID_ALIAS));
    }

    @Test
    public void parseAlias_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_ALIAS, () -> ParserUtil.parseAlias(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_ALIAS, () -> ParserUtil.parseAlias(WHITESPACE));
    }

    @Test
    public void parseAlias_validValueWithoutWhitespace_returnsAlias() throws Exception {
        assertEquals(new Alias(VALID_ALIAS), ParserUtil.parseAlias(VALID_ALIAS));
    }

    @Test
    public void parseAlias_repeatedSpaces_returnsNormalisedAlias() throws Exception {
        assertEquals(new Alias("Alex Tan"), ParserUtil.parseAlias("  Alex   Tan  "));
    }

    @Test
    public void parseIntroducer_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIntroducer(null));
    }

    @Test
    public void parseIntroducer_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Introducer.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseIntroducer(
                INVALID_INTRODUCER));
    }

    @Test
    public void parseIntroducer_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_INTRODUCER, () -> ParserUtil.parseIntroducer(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_INTRODUCER, () -> ParserUtil.parseIntroducer(WHITESPACE));
    }

    @Test
    public void parseIntroducer_validValueWithoutWhitespace_returnsIntroducer() throws Exception {
        assertEquals(new Introducer(VALID_INTRODUCER), ParserUtil.parseIntroducer(VALID_INTRODUCER));
    }

    @Test
    public void parseIntroducer_repeatedSpaces_returnsNormalisedIntroducer() throws Exception {
        assertEquals(new Introducer("Ben Lee"), ParserUtil.parseIntroducer("  Ben   Lee  "));
    }

    @Test
    public void parseNote_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNote(null));
    }

    @Test
    public void parseNote_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseNote(INVALID_NOTE));
    }

    @Test
    public void parseNote_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_NOTE, () -> ParserUtil.parseNote(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_NOTE, () -> ParserUtil.parseNote(WHITESPACE));
    }

    @Test
    public void parseNote_validValueWithoutWhitespace_returnsNote() throws Exception {
        assertEquals(new Note(VALID_NOTE), ParserUtil.parseNote(VALID_NOTE));
    }

    @Test
    public void parseNote_validValueWithWhitespace_returnsTrimmedNote() throws Exception {
        assertEquals(new Note(VALID_NOTE), ParserUtil.parseNote("  " + VALID_NOTE + "  "));
    }

    @Test
    public void parseContext_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseContext(null));
    }

    @Test
    public void parseContext_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Context.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseContext(INVALID_CONTEXT));
    }

    @Test
    public void parseContext_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_CONTEXT, () -> ParserUtil.parseContext(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_CONTEXT, () -> ParserUtil.parseContext(WHITESPACE));
    }

    @Test
    public void parseContext_validValueWithoutWhitespace_returnsContext() throws Exception {
        assertEquals(new Context(VALID_CONTEXT_1), ParserUtil.parseContext(VALID_CONTEXT_1));
    }

    @Test
    public void parseContext_validValueWithWhitespace_returnsTrimmedContext() throws Exception {
        String contextWithWhitespace = WHITESPACE + VALID_CONTEXT_1 + WHITESPACE;
        assertEquals(new Context(VALID_CONTEXT_1), ParserUtil.parseContext(contextWithWhitespace));
    }

    @Test
    public void parseContext_repeatedSpaces_returnsCollapsedContext() throws Exception {
        assertEquals("NUS Hackers", ParserUtil.parseContext("NUS   Hackers").description);
    }

    @Test
    public void parseContext_overLimitOnlyBeforeCollapsing_returnsContext() throws Exception {
        String context = "a".repeat(25) + "   " + "a".repeat(24); // 52 characters, 50 once collapsed
        assertEquals("a".repeat(25) + " " + "a".repeat(24), ParserUtil.parseContext(context).description);
    }

    @Test
    public void parseContexts_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseContexts(null));
    }

    @Test
    public void parseContexts_collectionWithInvalidContexts_throwsParseException() {
        assertThrows(ParseException.class, () ->
                ParserUtil.parseContexts(List.of(VALID_CONTEXT_1, INVALID_CONTEXT)));
    }

    @Test
    public void parseContexts_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseContexts(List.of()).isEmpty());
    }

    @Test
    public void parseContexts_collectionWithValidContexts_returnsContextSet() throws Exception {
        Set<Context> actualContextSet = ParserUtil.parseContexts(List.of(VALID_CONTEXT_1, VALID_CONTEXT_2));
        Set<Context> expectedContextSet = Set.of(new Context(VALID_CONTEXT_1), new Context(VALID_CONTEXT_2));

        assertEquals(expectedContextSet, actualContextSet);
    }

    @Test
    public void parseContexts_duplicatesDifferingOnlyInCase_returnsSingleContext() throws Exception {
        assertEquals(1, ParserUtil.parseContexts(List.of("CS2103T", "cs2103t")).size());
    }

    @Test
    public void parseContexts_duplicatesDifferingOnlyInSpacing_returnsSingleContext() throws Exception {
        Set<Context> contexts = ParserUtil.parseContexts(List.of("NUS   Hackers", "nus hackers"));
        assertEquals(1, contexts.size());
        assertEquals("NUS Hackers", contexts.iterator().next().description);
    }
}
