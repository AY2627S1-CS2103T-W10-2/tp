package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Compares strings ignoring case, treating runs of ASCII digits as unsigned numbers.
 */
public final class NaturalOrderComparator implements Comparator<String> {

    public static final NaturalOrderComparator INSTANCE = new NaturalOrderComparator();

    private NaturalOrderComparator() {
    }

    /**
     * Compares numeric runs without integer overflow. If strings differ only in leading zeros,
     * compares their spellings ignoring case so distinct context names remain distinct map keys.
     *
     * @throws NullPointerException if either string is null.
     */
    @Override
    public int compare(String first, String second) {
        requireNonNull(first);
        requireNonNull(second);
        int firstIndex = 0;
        int secondIndex = 0;
        while (firstIndex < first.length() && secondIndex < second.length()) {
            char firstChar = first.charAt(firstIndex);
            char secondChar = second.charAt(secondIndex);
            if (isDigit(firstChar) && isDigit(secondChar)) {
                int firstEnd = digitRunEnd(first, firstIndex);
                int secondEnd = digitRunEnd(second, secondIndex);
                int comparison = new BigInteger(first.substring(firstIndex, firstEnd))
                        .compareTo(new BigInteger(second.substring(secondIndex, secondEnd)));
                if (comparison != 0) {
                    return comparison;
                }
                firstIndex = firstEnd;
                secondIndex = secondEnd;
            } else {
                int comparison = String.CASE_INSENSITIVE_ORDER.compare(
                        String.valueOf(firstChar), String.valueOf(secondChar));
                if (comparison != 0) {
                    return comparison;
                }
                firstIndex++;
                secondIndex++;
            }
        }
        int comparison = Integer.compare(first.length() - firstIndex, second.length() - secondIndex);
        return comparison != 0 ? comparison : String.CASE_INSENSITIVE_ORDER.compare(first, second);
    }

    private static boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private static int digitRunEnd(String text, int start) {
        int end = start;
        while (end < text.length() && isDigit(text.charAt(end))) {
            end++;
        }
        return end;
    }
}
