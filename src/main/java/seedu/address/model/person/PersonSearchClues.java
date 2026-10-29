package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

/**
 * Builds the field-specific clues used to search for a person.
 */
public class PersonSearchClues {
    private String nameClue;
    private String phoneClue;
    private String emailClue;
    private List<String> contextClues = List.of();
    private String aliasClue;
    private String introducerClue;
    private String noteClue;

    /**
     * Sets the name clue.
     */
    public PersonSearchClues withNameClue(String nameClue) {
        this.nameClue = requireNonNull(nameClue);
        return this;
    }

    /**
     * Sets the phone clue.
     */
    public PersonSearchClues withPhoneClue(String phoneClue) {
        this.phoneClue = requireNonNull(phoneClue);
        return this;
    }

    /**
     * Sets the email clue.
     */
    public PersonSearchClues withEmailClue(String emailClue) {
        this.emailClue = requireNonNull(emailClue);
        return this;
    }

    /**
     * Sets the context clues using a defensive copy.
     */
    public PersonSearchClues withContextClues(List<String> contextClues) {
        requireNonNull(contextClues);
        contextClues.forEach(clue -> requireNonNull(clue));
        this.contextClues = List.copyOf(contextClues);
        return this;
    }

    /**
     * Sets the alias clue.
     */
    public PersonSearchClues withAliasClue(String aliasClue) {
        this.aliasClue = requireNonNull(aliasClue);
        return this;
    }

    /**
     * Sets the introducer clue.
     */
    public PersonSearchClues withIntroducerClue(String introducerClue) {
        this.introducerClue = requireNonNull(introducerClue);
        return this;
    }

    /**
     * Sets the note clue.
     */
    public PersonSearchClues withNoteClue(String noteClue) {
        this.noteClue = requireNonNull(noteClue);
        return this;
    }

    public Optional<String> getNameClue() {
        return Optional.ofNullable(nameClue);
    }

    public Optional<String> getPhoneClue() {
        return Optional.ofNullable(phoneClue);
    }

    public Optional<String> getEmailClue() {
        return Optional.ofNullable(emailClue);
    }

    public List<String> getContextClues() {
        return contextClues;
    }

    public Optional<String> getAliasClue() {
        return Optional.ofNullable(aliasClue);
    }

    public Optional<String> getIntroducerClue() {
        return Optional.ofNullable(introducerClue);
    }

    public Optional<String> getNoteClue() {
        return Optional.ofNullable(noteClue);
    }

    /**
     * Returns true if no field-specific clue has been supplied.
     */
    public boolean isEmpty() {
        return nameClue == null && phoneClue == null && emailClue == null && contextClues.isEmpty()
                && aliasClue == null && introducerClue == null && noteClue == null;
    }
}
