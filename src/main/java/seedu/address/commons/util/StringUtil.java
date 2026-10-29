package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    private static final Pattern WHITESPACE_RUN = Pattern.compile("\\s+");

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns {@code text} trimmed, with each run of whitespace inside it replaced by a single space.
     *   <br>examples:<pre>
     *       collapseWhitespace("  Alex   Tan ") == "Alex Tan"
     *       collapseWhitespace("CS2040S\tLab") == "CS2040S Lab"
     *       </pre>
     * @param text cannot be null.
     */
    public static String collapseWhitespace(String text) {
        requireNonNull(text);
        return WHITESPACE_RUN.matcher(text.trim()).replaceAll(" ");
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        // Integer#parseInt accepts non-ASCII digits such as "１", which the user guide does not allow
        if (!s.chars().allMatch(c -> c >= '0' && c <= '9')) {
            return false;
        }
        try {
            return Integer.parseInt(s) > 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
