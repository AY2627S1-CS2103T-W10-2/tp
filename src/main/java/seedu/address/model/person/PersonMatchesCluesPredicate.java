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
    private final List<String> contextClues;

    /**
     * Creates a predicate with an optional name clue and zero or more context clues.
     */
    public PersonMatchesCluesPredicate(Optional<String> nameClue, List<String> contextClues) {
        requireNonNull(nameClue);
        requireNonNull(contextClues);
        contextClues.forEach(clue -> requireNonNull(clue));
        this.nameClue = nameClue;
        this.contextClues = List.copyOf(contextClues);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        boolean matchesName = nameClue
                .map(clue -> containsIgnoreCase(person.getName().fullName, clue))
                .orElse(true);
        boolean matchesAllContexts = contextClues.stream()
                .allMatch(clue -> person.getContexts().stream()
                        .anyMatch(context -> containsIgnoreCase(context.description, clue)));
        return matchesName && matchesAllContexts;
    }

    private static boolean containsIgnoreCase(String value, String clue) {
        return value.toLowerCase(Locale.ROOT).contains(clue.toLowerCase(Locale.ROOT));
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
                && contextClues.equals(otherPredicate.contextClues);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameClue", nameClue.orElse(null))
                .add("contextClues", contextClues)
                .toString();
    }
}
