package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers: blank
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("+")); // plus only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // spaces within digits
        assertFalse(Phone.isValidPhone("1234567890123456")); // 16 numbers

        // invalid phone numbers: disallowed characters
        assertFalse(Phone.isValidPhone("phone")); // letters only
        assertFalse(Phone.isValidPhone("9011p041")); // letter within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // space within digits
        assertFalse(Phone.isValidPhone("9123-4567")); // hyphen within digits

        // invalid phone numbers: misplaced plus
        assertFalse(Phone.isValidPhone("+")); // plus only
        assertFalse(Phone.isValidPhone("9123+4567")); // plus not at start
        assertFalse(Phone.isValidPhone("++6591234567")); // more than one plus

        // invalid phone numbers: wrong number of digits
        assertFalse(Phone.isValidPhone("91")); // 2 digits, one under the minimum
        assertFalse(Phone.isValidPhone("+12")); // plus does not count as a digit
        assertFalse(Phone.isValidPhone("1234567890123456")); // 16 digits, one over the maximum
        assertFalse(Phone.isValidPhone("+1234567890123456")); // 16 digits with plus

        // valid phone numbers: examples from spec
        assertTrue(Phone.isValidPhone("91234567"));
        assertTrue(Phone.isValidPhone("+6591234567"));
        assertTrue(Phone.isValidPhone("+14155550123"));

        // valid phone numbers: length boundaries
        assertTrue(Phone.isValidPhone("911")); // 3 digits, minimum
        assertTrue(Phone.isValidPhone("123456789012345")); // 15 digits, maximum
        assertTrue(Phone.isValidPhone("+911")); // minimum with plus
        assertTrue(Phone.isValidPhone("+123456789012345")); // maximum with plus
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }
}
