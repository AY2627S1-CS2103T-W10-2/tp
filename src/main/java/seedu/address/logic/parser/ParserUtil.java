package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Alias;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Introducer;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_EMPTY_NAME = "Name cannot be empty.";
    public static final String MESSAGE_EMPTY_CONTEXT = "Contexts cannot be empty.";
    public static final String MESSAGE_EMPTY_PHONE = "Phone number cannot be empty.";
    public static final String MESSAGE_EMPTY_EMAIL = "Email cannot be empty.";
    public static final String MESSAGE_EMPTY_ALIAS = "Alias cannot be empty.";
    public static final String MESSAGE_EMPTY_INTRODUCER = "Introducer cannot be empty.";
    public static final String MESSAGE_EMPTY_NOTE = "Note cannot be empty.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        if (name.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_NAME);
        }
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        if (phone.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_PHONE);
        }
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        if (email.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_EMAIL);
        }
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String context} into a {@code Context}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code context} is invalid.
     */
    public static Context parseContext(String context) throws ParseException {
        requireNonNull(context);
        if (context.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_CONTEXT);
        }
        String trimmedContext = context.trim();
        if (!Context.isValidDescription(trimmedContext)) {
            throw new ParseException(Context.MESSAGE_CONSTRAINTS);
        }
        return new Context(trimmedContext);
    }

    /**
     * Parses {@code Collection<String> contexts} into a {@code Set<Context>}.
     */
    public static Set<Context> parseContexts(Collection<String> contexts) throws ParseException {
        requireNonNull(contexts);
        final Set<Context> contextSet = new LinkedHashSet<>();
        for (String context : contexts) {
            contextSet.add(parseContext(context));
        }
        return contextSet;
    }

    /**
     * Parses an optional alias, rejecting an empty or invalid value.
     */
    public static Alias parseAlias(String alias) throws ParseException {
        requireNonNull(alias);
        if (alias.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_ALIAS);
        }
        if (!Alias.isValidAlias(alias)) {
            throw new ParseException(Alias.MESSAGE_CONSTRAINTS);
        }
        return new Alias(alias);
    }

    /**
     * Parses an optional introducer name, rejecting an empty or invalid value.
     */
    public static Introducer parseIntroducer(String introducer) throws ParseException {
        requireNonNull(introducer);
        if (introducer.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_INTRODUCER);
        }
        if (!Introducer.isValidIntroducer(introducer)) {
            throw new ParseException(Introducer.MESSAGE_CONSTRAINTS);
        }
        return new Introducer(introducer);
    }

    /**
     * Parses an optional personal note, rejecting an empty or invalid value.
     */
    public static Note parseNote(String note) throws ParseException {
        requireNonNull(note);
        if (note.isBlank()) {
            throw new ParseException(MESSAGE_EMPTY_NOTE);
        }
        if (!Note.isValidNote(note)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(note);
    }

    /**
     * Parses the value of {@code prefix} in {@code argMultimap} with {@code parser}.
     * Returns an empty {@code Optional} if {@code prefix} is absent.
     * The field parsers throw a checked exception, so {@code Optional#map} cannot be used instead.
     *
     * @throws ParseException if {@code parser} rejects the value.
     */
    public static <T> Optional<T> parseIfPresent(ArgumentMultimap argMultimap, Prefix prefix,
            FieldParser<T> parser) throws ParseException {
        requireNonNull(argMultimap);
        requireNonNull(prefix);
        requireNonNull(parser);
        Optional<String> value = argMultimap.getValue(prefix);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parser.parse(value.get()));
    }
}
