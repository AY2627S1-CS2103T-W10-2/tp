package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_PHONE_REMOVE = new Prefix("-p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_EMAIL_REMOVE = new Prefix("-e/");
    public static final Prefix PREFIX_CONTEXT = new Prefix("c/");
    public static final Prefix PREFIX_CONTEXT_ADD = new Prefix("+c/");
    public static final Prefix PREFIX_CONTEXT_REMOVE = new Prefix("-c/");
    public static final Prefix PREFIX_ALIAS = new Prefix("a/");
    public static final Prefix PREFIX_ALIAS_REMOVE = new Prefix("-a/");
    public static final Prefix PREFIX_INTRODUCER = new Prefix("i/");
    public static final Prefix PREFIX_INTRODUCER_REMOVE = new Prefix("-i/");
    public static final Prefix PREFIX_NOTE = new Prefix("nt/");
    public static final Prefix PREFIX_NOTE_REMOVE = new Prefix("-nt/");

}
