package seedu.address.testutil;

import java.util.List;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSearchClues;

/**
 * Test data shared by tests for the find feature.
 */
public final class FindTestUtil {

    public static final Person TONY = new PersonBuilder()
            .withName("Tony Zhang")
            .withPhone("91234567")
            .withEmail("tony@example.com")
            .withContexts("CS2103T", "Hack&Roll")
            .withAlias("Toto")
            .withIntroducer("Alice Tan")
            .withNote("Met my project  teammate at Hack&Roll")
            .build();

    private FindTestUtil() {}

    /**
     * Returns a fresh set of clues that matches every supported field of {@link #TONY}.
     */
    public static PersonSearchClues createFullSearchClues() {
        return new PersonSearchClues()
                .withNameClue("ton")
                .withPhoneClue("1234")
                .withEmailClue("@example")
                .withContextClues(List.of("2103", "hack"))
                .withAliasClue("tot")
                .withIntroducerClue("alice")
                .withNoteClue("project teammate");
    }
}
