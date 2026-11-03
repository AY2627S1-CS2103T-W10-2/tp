package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class NaturalOrderComparatorTest {

    private final NaturalOrderComparator comparator = NaturalOrderComparator.INSTANCE;

    @Test
    public void compare_numberedContexts_sortsNumericallyIgnoringCase() {
        List<String> contexts = List.of("Tutorial 10", "tutorial 2", "Tutorial 1", "Group 11", "group 9");
        assertEquals(List.of("group 9", "Group 11", "Tutorial 1", "tutorial 2", "Tutorial 10"),
                contexts.stream().sorted(comparator).toList());
    }

    @Test
    public void compare_multipleDigitRuns_comparesEachNumber() {
        assertBefore("Week 2 Group 9", "Week 2 Group 11");
        assertBefore("Week 2 Group 11", "Week 10 Group 1");
        assertBefore("CS2T", "CS10T");
    }

    @Test
    public void compare_largeNumbers_doesNotOverflow() {
        assertBefore("Group 9223372036854775808", "Group 10000000000000000000");
    }

    @Test
    public void compare_leadingZeros_preservesDistinctNamesAndComparesSuffixes() {
        assertBefore("Group 02", "Group 2");
        assertBefore("Group 0", "Group 00");
        assertBefore("Group 02 A", "Group 2 B");
        assertBefore("Group 02", "Group 10");
    }

    @Test
    public void compare_caseDifferences_returnsZero() {
        assertEquals(0, comparator.compare("Tutorial 2", "tUtOrIaL 2"));
        assertEquals(0, comparator.compare("Group 02", "group 02"));
    }

    @Test
    public void compare_textAndPrefixes_preservesAlphabeticalOrder() {
        assertBefore("", "Alpha");
        assertBefore("Alpha", "alpha 1");
        assertBefore("Group 2", "Group 2A");
        assertBefore("Group-2", "Group2");
        assertBefore("Group2", "GroupA");
    }

    @Test
    public void compare_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> comparator.compare(null, "Group 2"));
        assertThrows(NullPointerException.class, () -> comparator.compare("Group 2", null));
    }

    private void assertBefore(String first, String second) {
        assertTrue(comparator.compare(first, second) < 0);
        assertTrue(comparator.compare(second, first) > 0);
    }
}
