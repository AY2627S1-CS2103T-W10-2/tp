package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADD_CONTEXT_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.ADD_CONTEXT_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMPTY_ADD_CONTEXT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.EMPTY_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.EMPTY_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.EMPTY_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.EMPTY_REMOVE_CONTEXT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADD_CONTEXT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.REMOVE_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.REMOVE_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.REMOVE_CONTEXT_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CONTEXT_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_REMOVE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String PLAIN_CONTEXT_DESC = " " + PREFIX_CONTEXT + VALID_CONTEXT_FRIEND;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADD_CONTEXT_DESC, Context.MESSAGE_CONSTRAINTS); // invalid context
        assertParseFailure(parser, "1" + EMPTY_NAME_DESC, ParserUtil.MESSAGE_EMPTY_NAME); // empty name
        // empty phone or email points to -p/ or -e/
        assertParseFailure(parser, "1" + EMPTY_PHONE_DESC, EditCommandParser.MESSAGE_EMPTY_PHONE);
        assertParseFailure(parser, "1" + EMPTY_EMAIL_DESC, EditCommandParser.MESSAGE_EMPTY_EMAIL);

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // an empty context alongside valid contexts is still invalid
        assertParseFailure(parser, "1" + ADD_CONTEXT_DESC_FRIEND + ADD_CONTEXT_DESC_HUSBAND + EMPTY_ADD_CONTEXT_DESC,
                ParserUtil.MESSAGE_EMPTY_CONTEXT);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + ADD_CONTEXT_DESC_HUSBAND
                + EMAIL_DESC_AMY + NAME_DESC_AMY + ADD_CONTEXT_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY)
                .withContextsToAdd(VALID_CONTEXT_HUSBAND, VALID_CONTEXT_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // context to add
        userInput = targetIndex.getOneBased() + ADD_CONTEXT_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withContextsToAdd(VALID_CONTEXT_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // context to remove
        userInput = targetIndex.getOneBased() + REMOVE_CONTEXT_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withContextsToRemove(VALID_CONTEXT_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_plainContextPrefix_failure() {
        // before the index is checked, so the hint is shown even right after INDEX
        assertParseFailure(parser, "1" + PLAIN_CONTEXT_DESC, EditCommandParser.MESSAGE_INVALID_CONTEXT_PREFIX);
        assertParseFailure(parser, "1" + NAME_DESC_AMY + PLAIN_CONTEXT_DESC,
                EditCommandParser.MESSAGE_INVALID_CONTEXT_PREFIX);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonContextValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + EMAIL_DESC_AMY
                + ADD_CONTEXT_DESC_FRIEND + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADD_CONTEXT_DESC_FRIEND
                + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADD_CONTEXT_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));
    }

    @Test
    public void parse_emptyContext_failure() {
        // +c/ and -c/ need a context after them
        assertParseFailure(parser, INDEX_THIRD_PERSON.getOneBased() + EMPTY_ADD_CONTEXT_DESC,
                ParserUtil.MESSAGE_EMPTY_CONTEXT);
        assertParseFailure(parser, INDEX_THIRD_PERSON.getOneBased() + EMPTY_REMOVE_CONTEXT_DESC,
                ParserUtil.MESSAGE_EMPTY_CONTEXT);
    }

    @Test
    public void parse_removePhoneOrEmail_success() {
        // -p/ alone; it is not read as an empty p/
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhoneRemoved().build();
        assertParseSuccess(parser, "1" + REMOVE_PHONE_DESC, new EditCommand(INDEX_FIRST_PERSON, descriptor));

        // rename and remove the email in one command
        descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).withEmailRemoved().build();
        assertParseSuccess(parser, "3" + NAME_DESC_AMY + REMOVE_EMAIL_DESC,
                new EditCommand(INDEX_THIRD_PERSON, descriptor));

        // remove both
        descriptor = new EditPersonDescriptorBuilder().withPhoneRemoved().withEmailRemoved().build();
        assertParseSuccess(parser, "1" + REMOVE_PHONE_DESC + REMOVE_EMAIL_DESC,
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_removeWithValue_failure() {
        assertParseFailure(parser, "1" + REMOVE_PHONE_DESC + "91234567",
                EditCommandParser.MESSAGE_REMOVE_PHONE_WITH_VALUE);
        assertParseFailure(parser, "1" + REMOVE_EMAIL_DESC + "amy@example.com",
                EditCommandParser.MESSAGE_REMOVE_EMAIL_WITH_VALUE);
    }

    @Test
    public void parse_setAndRemoveSameField_failure() {
        assertParseFailure(parser, "1" + PHONE_DESC_AMY + REMOVE_PHONE_DESC,
                EditCommandParser.MESSAGE_SET_AND_REMOVE_PHONE);
        assertParseFailure(parser, "1" + EMAIL_DESC_AMY + REMOVE_EMAIL_DESC,
                EditCommandParser.MESSAGE_SET_AND_REMOVE_EMAIL);

        // the conflict is reported before an empty or invalid phone
        assertParseFailure(parser, "1" + EMPTY_PHONE_DESC + REMOVE_PHONE_DESC,
                EditCommandParser.MESSAGE_SET_AND_REMOVE_PHONE);
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + REMOVE_PHONE_DESC,
                EditCommandParser.MESSAGE_SET_AND_REMOVE_PHONE);
    }

    @Test
    public void parse_repeatedRemovePrefix_failure() {
        assertParseFailure(parser, "1" + REMOVE_PHONE_DESC + REMOVE_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_REMOVE));
    }
}
