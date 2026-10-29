package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches every supplied field-specific search clue.
 */
public class PersonMatchesCluesPredicate implements Predicate<Person> {
    private final Optional<String> nameClue;
    private final Optional<String> phoneClue;
    private final Optional<String> emailClue;
    private final List<String> contextClues;
    private final Optional<String> aliasClue;
    private final Optional<String> introducerClue;
    private final Optional<String> noteClue;

    /**
     * Creates a predicate from the supplied field-specific search clues.
     */
    public PersonMatchesCluesPredicate(PersonSearchClues searchClues) {
        requireNonNull(searchClues);
        assert !searchClues.isEmpty() : "At least one structured search clue must be provided";
        this.nameClue = searchClues.getNameClue();
        this.phoneClue = searchClues.getPhoneClue();
        this.emailClue = searchClues.getEmailClue();
        this.contextClues = List.copyOf(searchClues.getContextClues());
        this.aliasClue = searchClues.getAliasClue();
        this.introducerClue = searchClues.getIntroducerClue();
        this.noteClue = searchClues.getNoteClue();
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        boolean matchesName = nameClue
                .map(clue -> containsIgnoreCase(person.getName().fullName, clue))
                .orElse(true);
        boolean matchesPhone = matchesOptionalField(person.getPhone(), phoneClue);
        boolean matchesEmail = matchesOptionalField(person.getEmail(), emailClue);
        boolean matchesAllContexts = contextClues.stream()
                .allMatch(clue -> person.getContexts().stream()
                        .anyMatch(context -> containsIgnoreCase(context.description, clue)));
        boolean matchesAlias = matchesOptionalField(person.getAlias(), aliasClue);
        boolean matchesIntroducer = matchesOptionalField(person.getIntroducer(), introducerClue);
        boolean matchesNote = matchesOptionalField(person.getNote(), noteClue);
        return matchesName && matchesPhone && matchesEmail && matchesAllContexts
                && matchesAlias && matchesIntroducer && matchesNote;
    }

    private static boolean matchesOptionalField(Optional<?> field, Optional<String> clue) {
        return clue.map(searchValue -> field
                .map(Object::toString)
                .map(fieldValue -> containsIgnoreCase(fieldValue, searchValue))
                .orElse(false))
                .orElse(true);
    }

    private static boolean containsIgnoreCase(String value, String clue) {
        return normaliseForSearch(value).contains(normaliseForSearch(clue));
    }

    private static String normaliseForSearch(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PersonMatchesCluesPredicate otherPredicate)) {
            return false;
        }

        return nameClue.equals(otherPredicate.nameClue)
                && phoneClue.equals(otherPredicate.phoneClue)
                && emailClue.equals(otherPredicate.emailClue)
                && contextClues.equals(otherPredicate.contextClues)
                && aliasClue.equals(otherPredicate.aliasClue)
                && introducerClue.equals(otherPredicate.introducerClue)
                && noteClue.equals(otherPredicate.noteClue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameClue", nameClue.orElse(null))
                .add("phoneClue", phoneClue.orElse(null))
                .add("emailClue", emailClue.orElse(null))
                .add("contextClues", contextClues)
                .add("aliasClue", aliasClue.orElse(null))
                .add("introducerClue", introducerClue.orElse(null))
                .add("noteClue", noteClue.orElse(null))
                .toString();
    }
}
