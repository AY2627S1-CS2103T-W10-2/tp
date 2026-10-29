package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesCluesPredicateTest {

    private static final Person TONY = new PersonBuilder()
            .withName("Tony Zhang")
            .withPhone("91234567")
            .withEmail("tony@example.com")
            .withContexts("CS2103T", "Hack&Roll")
            .withAlias("Toto")
            .withIntroducer("Alice Tan")
            .withNote("Met my project  teammate at Hack&Roll")
            .build();

    @Test
    public void constructor_noClues_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new PersonMatchesCluesPredicate(new PersonSearchClues()));
    }

    @Test
    public void equals() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(createFullSearchClues());

        // same object -> returns true
        assertTrue(predicate.equals(predicate));

        // same values -> returns true
        assertTrue(predicate.equals(new PersonMatchesCluesPredicate(createFullSearchClues())));

        // differs only in name clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withNameClue("Zhang"))));

        // differs only in phone clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withPhoneClue("8765"))));

        // differs only in email clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withEmailClue("@u.nus.edu"))));

        // differs only in context clues -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withContextClues(List.of("Hack")))));

        // differs only in alias clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withAliasClue("TZ"))));

        // differs only in introducer clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withIntroducerClue("Bob"))));

        // differs only in note clue -> returns false
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                createFullSearchClues().withNoteClue("internship"))));

        // different types -> returns false
        assertFalse(predicate.equals(1));

        // null -> returns false
        assertFalse(predicate.equals(null));
    }

    @Test
    public void test_nameAndAllContextsMatch_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues()
                        .withNameClue("ton")
                        .withContextClues(List.of("2103", "hack")));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_partialAndMixedCaseCluesMatch_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues()
                        .withNameClue("zHaNg")
                        .withContextClues(List.of("cS2103")));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_allSupportedCluesMatch_returnsTrue() {
        PersonSearchClues searchClues = new PersonSearchClues()
                .withNameClue("ton")
                .withPhoneClue("1234")
                .withEmailClue("@EXAMPLE.COM")
                .withContextClues(List.of("2103", "hack"))
                .withAliasClue("tot")
                .withIntroducerClue("alice")
                .withNoteClue("project teammate");
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(searchClues);

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_eachAdditionalFieldMatchesOwnClue_returnsTrue() {
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withPhoneClue("1234")).test(TONY));
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withEmailClue("@example")).test(TONY));
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withAliasClue("toto")).test(TONY));
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withIntroducerClue("tan")).test(TONY));
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNoteClue("hack&roll")).test(TONY));
    }

    @Test
    public void test_missingOptionalFieldWithCorrespondingClue_returnsFalse() {
        Person personWithMissingOptionalFields = new PersonBuilder()
                .withoutPhone()
                .withoutEmail()
                .build();

        assertFalse(new PersonMatchesCluesPredicate(new PersonSearchClues().withPhoneClue("1234"))
                .test(personWithMissingOptionalFields));
        assertFalse(new PersonMatchesCluesPredicate(new PersonSearchClues().withEmailClue("@example"))
                .test(personWithMissingOptionalFields));
        assertFalse(new PersonMatchesCluesPredicate(new PersonSearchClues().withAliasClue("toto"))
                .test(personWithMissingOptionalFields));
        assertFalse(new PersonMatchesCluesPredicate(new PersonSearchClues().withIntroducerClue("tan"))
                .test(personWithMissingOptionalFields));
        assertFalse(new PersonMatchesCluesPredicate(new PersonSearchClues().withNoteClue("project"))
                .test(personWithMissingOptionalFields));
    }

    @Test
    public void test_nameOnlyMatches_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNameClue("Tony"));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_nameSavedWithRepeatedSpaces_matchesFullNameClue() {
        Person alex = new PersonBuilder().withName("Alex  Tan").withContexts("CS2040S  Lab").build();

        assertTrue(new PersonMatchesCluesPredicate(new PersonSearchClues().withNameClue("Alex Tan")).test(alex));
        assertTrue(new PersonMatchesCluesPredicate(new PersonSearchClues().withNameClue("Alex  Tan")).test(alex));
        assertTrue(new PersonMatchesCluesPredicate(
                new PersonSearchClues().withContextClues(List.of("CS2040S Lab"))).test(alex));
    }

    @Test
    public void test_contextOnlyMatches_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withContextClues(List.of("Hack&Roll")));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_nameDoesNotMatch_returnsFalse() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues()
                        .withNameClue("Alice")
                        .withContextClues(List.of("CS2103T")));

        assertFalse(predicate.test(TONY));
    }

    @Test
    public void test_oneContextClueDoesNotMatch_returnsFalse() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues()
                        .withNameClue("Tony")
                        .withContextClues(List.of("CS2103T", "Internship")));

        assertFalse(predicate.test(TONY));
    }

    @Test
    public void test_cluesDoNotSearchOtherFields_returnsFalse() {
        PersonMatchesCluesPredicate contextAsNameCluePredicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNameClue("CS2103T"));
        PersonMatchesCluesPredicate nameAsContextCluePredicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withContextClues(List.of("Tony")));
        PersonMatchesCluesPredicate phoneAsNameCluePredicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNameClue("91234567"));
        PersonMatchesCluesPredicate emailAsNameCluePredicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues().withNameClue("tony@example.com"));

        assertFalse(contextAsNameCluePredicate.test(TONY));
        assertFalse(nameAsContextCluePredicate.test(TONY));
        assertFalse(phoneAsNameCluePredicate.test(TONY));
        assertFalse(emailAsNameCluePredicate.test(TONY));
    }

    @Test
    public void toStringMethod() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                new PersonSearchClues()
                        .withNameClue("Tony")
                        .withContextClues(List.of("CS2103T")));

        String expected = PersonMatchesCluesPredicate.class.getCanonicalName()
                + "{nameClue=Tony, phoneClue=null, emailClue=null, contextClues=[CS2103T], "
                + "aliasClue=null, introducerClue=null, noteClue=null}";
        assertEquals(expected, predicate.toString());
    }

    private static PersonSearchClues createFullSearchClues() {
        return new PersonSearchClues()
                .withNameClue("Tony")
                .withPhoneClue("9123")
                .withEmailClue("@example.com")
                .withContextClues(List.of("CS2103T"))
                .withAliasClue("Toto")
                .withIntroducerClue("Alice")
                .withNoteClue("project");
    }
}
