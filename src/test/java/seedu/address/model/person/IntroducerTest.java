package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class IntroducerTest {

    @Test
    public void constructor_normalisesSpaces() {
        assertEquals("Ben Lee", new Introducer("  Ben   Lee  ").value);
    }

    @Test
    public void constructor_collapsesTabsAndOtherWhitespace() {
        assertEquals("Ben Lee", new Introducer("Ben\tLee").value);
        assertEquals("Ben Lee", new Introducer("\t Ben \t\n Lee\r\n").value);
    }

    @Test
    public void constructor_invalidIntroducer_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Introducer(null));
        assertThrows(IllegalArgumentException.class, () -> new Introducer("  "));
        assertThrows(IllegalArgumentException.class, () -> new Introducer("Ben123"));
        assertThrows(IllegalArgumentException.class, () -> new Introducer("B".repeat(101)));
    }

    @Test
    public void isValidIntroducer() {
        assertFalse(Introducer.isValidIntroducer(null));
        assertFalse(Introducer.isValidIntroducer("@Ben"));
        assertTrue(Introducer.isValidIntroducer(" Ben O'Brien "));
        assertTrue(Introducer.isValidIntroducer("Ben\tLee")); // tab counts as a space
        assertFalse(Introducer.isValidIntroducer("\t"));
        assertFalse(Introducer.isValidIntroducer("Ben\t123"));
        assertTrue(Introducer.isValidIntroducer("B".repeat(100)));
    }

    @Test
    public void equals_normalisedValues() {
        Introducer introducer = new Introducer("Ben Lee");
        Introducer sameIntroducer = new Introducer(" Ben   Lee ");
        assertEquals(introducer, sameIntroducer);
        assertEquals(introducer.hashCode(), sameIntroducer.hashCode());
        assertEquals(introducer, new Introducer("Ben\tLee"));
        assertFalse(introducer.equals(new Introducer("Ben Lim")));
    }
}
