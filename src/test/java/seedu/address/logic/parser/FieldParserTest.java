package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Phone;

public class FieldParserTest {

    private static final FieldParser<Phone> PHONE_PARSER = ParserUtil::parsePhone;

    @Test
    public void parse_validValue_returnsParsedValue() throws Exception {
        assertEquals(new Phone("123456"), PHONE_PARSER.parse("123456"));
    }

    @Test
    public void parse_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> PHONE_PARSER.parse("+65"));
    }
}
