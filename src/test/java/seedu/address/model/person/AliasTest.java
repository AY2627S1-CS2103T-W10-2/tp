package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AliasTest {

    @Test
    public void constructor_normalisesSpaces() {
        assertEquals("Alex Tan", new Alias("  Alex   Tan  ").value);
    }

    @Test
    public void constructor_invalidAlias_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Alias(null));
        assertThrows(IllegalArgumentException.class, () -> new Alias("  "));
        assertThrows(IllegalArgumentException.class, () -> new Alias("Alex123"));
        assertThrows(IllegalArgumentException.class, () -> new Alias("A".repeat(101)));
    }

    @Test
    public void isValidAlias() {
        assertFalse(Alias.isValidAlias(null));
        assertFalse(Alias.isValidAlias("-Alex"));
        assertTrue(Alias.isValidAlias("  Jean-Luc  "));
        assertTrue(Alias.isValidAlias("A".repeat(100)));
    }

    @Test
    public void equals_normalisedValues() {
        Alias alias = new Alias("Alex Tan");
        Alias sameAlias = new Alias(" Alex   Tan ");
        assertEquals(alias, sameAlias);
        assertEquals(alias.hashCode(), sameAlias.hashCode());
        assertFalse(alias.equals(new Alias("Alex Lim")));
    }
}
