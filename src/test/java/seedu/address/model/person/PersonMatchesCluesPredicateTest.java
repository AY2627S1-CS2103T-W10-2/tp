package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

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
                new PersonMatchesCluesPredicate(Optional.empty(), List.of()));
    }

    @Test
    public void equals() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("CS2103T"));

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("CS2103T"))));
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                Optional.of("Zhang"), List.of("CS2103T"))));
        assertFalse(predicate.equals(new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("Hack"))));
        assertFalse(predicate.equals(1));
        assertFalse(predicate.equals(null));
    }

    @Test
    public void test_nameAndAllContextsMatch_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("ton"), List.of("2103", "hack"));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_partialAndMixedCaseCluesMatch_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("zHaNg"), List.of("cS2103"));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_allSupportedCluesMatch_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("ton"), Optional.of("1234"), Optional.of("@EXAMPLE.COM"),
                List.of("2103", "hack"), Optional.of("tot"), Optional.of("alice"),
                Optional.of("project teammate"));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_eachAdditionalFieldMatchesOwnClue_returnsTrue() {
        assertTrue(createPredicate(Optional.empty(), Optional.of("1234"), Optional.empty(),
                List.of(), Optional.empty(), Optional.empty(), Optional.empty()).test(TONY));
        assertTrue(createPredicate(Optional.empty(), Optional.empty(), Optional.of("@example"),
                List.of(), Optional.empty(), Optional.empty(), Optional.empty()).test(TONY));
        assertTrue(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.of("toto"), Optional.empty(), Optional.empty()).test(TONY));
        assertTrue(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.empty(), Optional.of("tan"), Optional.empty()).test(TONY));
        assertTrue(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.empty(), Optional.empty(), Optional.of("hack&roll")).test(TONY));
    }

    @Test
    public void test_missingOptionalFieldWithCorrespondingClue_returnsFalse() {
        Person personWithMissingOptionalFields = new PersonBuilder()
                .withoutPhone()
                .withoutEmail()
                .build();

        assertFalse(createPredicate(Optional.empty(), Optional.of("1234"), Optional.empty(),
                List.of(), Optional.empty(), Optional.empty(), Optional.empty())
                .test(personWithMissingOptionalFields));
        assertFalse(createPredicate(Optional.empty(), Optional.empty(), Optional.of("@example"),
                List.of(), Optional.empty(), Optional.empty(), Optional.empty())
                .test(personWithMissingOptionalFields));
        assertFalse(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.of("toto"), Optional.empty(), Optional.empty())
                .test(personWithMissingOptionalFields));
        assertFalse(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.empty(), Optional.of("tan"), Optional.empty())
                .test(personWithMissingOptionalFields));
        assertFalse(createPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(), Optional.empty(), Optional.empty(), Optional.of("project"))
                .test(personWithMissingOptionalFields));
    }

    @Test
    public void test_nameOnlyMatches_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of());

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_contextOnlyMatches_returnsTrue() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.empty(), List.of("Hack&Roll"));

        assertTrue(predicate.test(TONY));
    }

    @Test
    public void test_nameDoesNotMatch_returnsFalse() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Alice"), List.of("CS2103T"));

        assertFalse(predicate.test(TONY));
    }

    @Test
    public void test_oneContextClueDoesNotMatch_returnsFalse() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("CS2103T", "Internship"));

        assertFalse(predicate.test(TONY));
    }

    @Test
    public void test_cluesDoNotSearchOtherFields_returnsFalse() {
        PersonMatchesCluesPredicate contextAsNameCluePredicate = new PersonMatchesCluesPredicate(
                Optional.of("CS2103T"), List.of());
        PersonMatchesCluesPredicate nameAsContextCluePredicate = new PersonMatchesCluesPredicate(
                Optional.empty(), List.of("Tony"));
        PersonMatchesCluesPredicate phoneAsNameCluePredicate = new PersonMatchesCluesPredicate(
                Optional.of("91234567"), List.of());
        PersonMatchesCluesPredicate emailAsNameCluePredicate = new PersonMatchesCluesPredicate(
                Optional.of("tony@example.com"), List.of());

        assertFalse(contextAsNameCluePredicate.test(TONY));
        assertFalse(nameAsContextCluePredicate.test(TONY));
        assertFalse(phoneAsNameCluePredicate.test(TONY));
        assertFalse(emailAsNameCluePredicate.test(TONY));
    }

    @Test
    public void toStringMethod() {
        PersonMatchesCluesPredicate predicate = new PersonMatchesCluesPredicate(
                Optional.of("Tony"), List.of("CS2103T"));

        String expected = PersonMatchesCluesPredicate.class.getCanonicalName()
                + "{nameClue=Tony, phoneClue=null, emailClue=null, contextClues=[CS2103T], "
                + "aliasClue=null, introducerClue=null, noteClue=null}";
        assertEquals(expected, predicate.toString());
    }

    private static PersonMatchesCluesPredicate createPredicate(Optional<String> nameClue,
            Optional<String> phoneClue, Optional<String> emailClue, List<String> contextClues,
            Optional<String> aliasClue, Optional<String> introducerClue, Optional<String> noteClue) {
        return new PersonMatchesCluesPredicate(nameClue, phoneClue, emailClue, contextClues,
                aliasClue, introducerClue, noteClue);
    }
}
