package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ContextTest {

    @Test
    public void constructor_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Context(null));
    }

    @Test
    public void constructor_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Context(""));
        assertThrows(IllegalArgumentException.class, () -> new Context("   "));
    }

    @Test
    public void constructor_trimsDescription() {
        assertEquals("CS2103T", new Context("  CS2103T ").description);
    }

    @Test
    public void constructor_collapsesRepeatedSpaces() {
        assertEquals("NUS Hackers", new Context("NUS   Hackers").description);
        assertEquals("Hack & Roll 2026", new Context("  Hack  &   Roll 2026 ").description);
    }

    @Test
    public void constructor_collapsesTabsAndOtherWhitespace() {
        assertEquals("CS2040S Lab", new Context("CS2040S\tLab").description);
        assertEquals("NUS Hackers", new Context("\tNUS \t \n Hackers\r\n").description);
    }

    @Test
    public void isValidDescription() {
        assertFalse(Context.isValidDescription(null));
        assertFalse(Context.isValidDescription(" "));
        assertTrue(Context.isValidDescription("CS2103T"));
        assertTrue(Context.isValidDescription("Hiking club 2026"));

        // tabs and other whitespace count as spaces
        assertTrue(Context.isValidDescription("CS2040S\tLab"));
        assertTrue(Context.isValidDescription("\tHiking \n club\t"));
        assertFalse(Context.isValidDescription("\t"));
        assertFalse(Context.isValidDescription("CS2103T/\tT09"));

        // spec examples
        assertTrue(Context.isValidDescription("Hack&Roll 2026"));
        assertTrue(Context.isValidDescription("NUS Hackers"));
        assertTrue(Context.isValidDescription("Google internship"));
        assertTrue(Context.isValidDescription("Tut #3"));
        assertFalse(Context.isValidDescription("CS2103T/T09")); // '/' not allowed
        assertFalse(Context.isValidDescription("(orbital)")); // parentheses not allowed

        // allowed special characters
        assertTrue(Context.isValidDescription("R&D-lab v1.2 O'Brien #5"));

        // must start with a letter or digit
        assertFalse(Context.isValidDescription("#3"));
        assertFalse(Context.isValidDescription("-club"));
        assertFalse(Context.isValidDescription("&Co"));
        assertTrue(Context.isValidDescription("3D printing"));

        // length is counted after trimming
        assertTrue(Context.isValidDescription("a".repeat(50)));
        assertTrue(Context.isValidDescription("  " + "a".repeat(50) + "  "));
        assertFalse(Context.isValidDescription("a".repeat(51)));

        // length is counted after collapsing repeated spaces
        assertTrue(Context.isValidDescription("a".repeat(25) + "   " + "a".repeat(24))); // 50 once collapsed
        assertFalse(Context.isValidDescription("a".repeat(25) + "   " + "a".repeat(25))); // 51 once collapsed
    }

    @Test
    public void constructor_invalidCharacters_throwsWithConstraintsMessage() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Context("(orbital)"));
        assertEquals(Context.MESSAGE_CONSTRAINTS, e.getMessage());
        assertEquals("Contexts should start with a letter or digit, contain only letters, digits, spaces and "
                + "& - . ' #, and be at most 50 characters long.", Context.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void constructor_keepsCaseAsTyped() {
        assertEquals("Hack&Roll 2026", new Context("Hack&Roll 2026").description);
    }

    @Test
    public void duplicateContextsInSet_keepsFirstSpelling() {
        Set<Context> contexts = new HashSet<>();
        assertTrue(contexts.add(new Context("CS2103T")));
        assertFalse(contexts.add(new Context("cs2103t")));
        assertEquals(1, contexts.size());
        assertEquals("CS2103T", contexts.iterator().next().description);
    }

    @Test
    public void equals_ignoresCaseAndSpaces() {
        Context context = new Context("CS2103T");
        assertEquals(context, new Context("cs2103t"));
        assertEquals(context, new Context(" CS2103T "));
        assertEquals(context.hashCode(), new Context("cs2103t").hashCode());
        assertEquals(new Context("NUS Hackers"), new Context("nus   hackers"));
        assertEquals(new Context("NUS Hackers").hashCode(), new Context("nus   hackers").hashCode());
        assertEquals(new Context("NUS Hackers"), new Context("nus\thackers"));
        assertEquals(new Context("NUS Hackers").hashCode(), new Context("nus\thackers").hashCode());
        assertNotEquals(context, new Context("CS2101"));
        assertNotEquals(context, null);
    }
}
