package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void isValidDescription() {
        assertFalse(Context.isValidDescription(null));
        assertFalse(Context.isValidDescription(" "));
        assertTrue(Context.isValidDescription("CS2103T"));
        assertTrue(Context.isValidDescription("Hiking club 2026"));
    }

    @Test
    public void equals_ignoresCaseAndSpaces() {
        Context context = new Context("CS2103T");
        assertEquals(context, new Context("cs2103t"));
        assertEquals(context, new Context(" CS2103T "));
        assertEquals(context.hashCode(), new Context("cs2103t").hashCode());
        assertNotEquals(context, new Context("CS2101"));
        assertNotEquals(context, null);
    }
}
