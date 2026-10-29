package seedu.address.logic;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists: %1$s has the same %2$s (%3$s).";
    public static final String MESSAGE_ARCHIVED_PERSON_HINT = "This person is archived; run archives to see them.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an error message indicating the duplicate person {@code existing}
     * and the phone or email it shares with {@code toCheck}.
     * If {@code existing} is archived, the message also says so.
     * {@code existing} must have the same identity as {@code toCheck}.
     */
    public static String getErrorMessageForDuplicatePerson(Person existing, Person toCheck) {
        String message;
        boolean isSamePhone = existing.getPhone().isPresent() && existing.getPhone().equals(toCheck.getPhone());
        if (isSamePhone) {
            message = String.format(MESSAGE_DUPLICATE_PERSON,
                    existing.getName(), "phone number", existing.getPhone().get());
        } else {
            // not a phone clash, so the two persons must share an email
            message = String.format(MESSAGE_DUPLICATE_PERSON,
                    existing.getName(), "email", existing.getEmail().get());
        }

        if (existing.isArchived()) {
            message += " " + MESSAGE_ARCHIVED_PERSON_HINT;
        }
        return message;
    }

    /**
     * Formats {@code contexts} as {@code [A] [B]}, in the order given, as in the person details shown to the
     * user.
     */
    public static String formatContexts(Collection<Context> contexts) {
        return contexts.stream()
                .map(context -> "[" + context + "]")
                .collect(Collectors.joining(" "));
    }

    /**
     * Formats {@code contexts} as {@code [A] [B]}, sorted by description ignoring case, as in the person
     * details shown to the user.
     */
    public static String formatContextsSorted(Collection<Context> contexts) {
        List<Context> sortedContexts = contexts.stream()
                .sorted(Comparator.comparing(Context::toString, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return formatContexts(sortedContexts);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Contexts: ")
                .append(formatContextsSorted(person.getContexts()));
        // a missing phone or email is left out rather than shown as an empty field
        person.getPhone().ifPresent(phone -> builder.append("; Phone: ").append(phone));
        person.getEmail().ifPresent(email -> builder.append("; Email: ").append(email));
        person.getAlias().ifPresent(alias -> builder.append("; Alias: ").append(alias));
        person.getIntroducer().ifPresent(introducer -> builder.append("; Introduced by: ").append(introducer));
        person.getNote().ifPresent(note -> builder.append("; Note: ").append(note));
        return builder.toString();
    }

}
