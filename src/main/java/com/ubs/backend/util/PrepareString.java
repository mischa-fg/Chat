package com.ubs.backend.util;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * A utility class to prepare Strings
 *
 * @author Marc Andri Fuchs
 * @author Tim Irmler
 * @since 19.07.2021
 */
public class PrepareString {
    /**
     * method to prepare and clean a string
     *
     * @param string                the string that will be prepared
     * @param maxStringLength       defines the maximum length of the string, if the string is longer than the max, the method will only return as many characters as described by maxStringLength
     * @param removeAllSpecialChars should special characters be removed?
     * @param toLowerCase           should the string be to lower case?
     * @param removeQuestionMark    should question mark be removed from the string?
     * @return the prepared string
     * @author Tim Irmler
     * @since 19.08.2021
     */
    public static String prepareString(String string, int maxStringLength, boolean removeAllSpecialChars, boolean toLowerCase, boolean removeQuestionMark) {
        String s = String.copyValueOf(string.toCharArray());

        printDebug("prepare string values", "maxStringLength = " + maxStringLength + ", removeAllSpecialChars = " + removeAllSpecialChars + ", toLowerCase = " + toLowerCase);
        printDebug("String before preparing", s);

        if (toLowerCase) {
            s = s.toLowerCase(Locale.ROOT);
        }

        Object[][] replacements;
        Object[] specialChars;
        if (removeQuestionMark) {
            specialChars = new Object[]{Pattern.compile("[^\\w\\d/ üöäéà]/gmi"), "", "reg"};
        } else {
            specialChars = new Object[]{Pattern.compile("[^\\w\\d/ üöäéà?]/gmi"), "", "reg"};
        }
        // handle User different from admin input
        Object[][] tempReplacements = new Object[][]{
                new String[]{"\\", "\\\\", "c"},
                new String[]{"\"", "\\\"", "c"},
                new String[]{"\u0026", "&", "c"},
                new Object[]{Pattern.compile("\\s+$"), "", "reg"},
                new String[]{"\n", "\\n", "c"},
        };

        if (removeAllSpecialChars) {
            replacements = new Object[tempReplacements.length + 1][3];
            System.arraycopy(tempReplacements, 0, replacements, 0, tempReplacements.length);
            replacements[replacements.length - 1] = specialChars;
        } else {
            replacements = tempReplacements;
        }

        for (Object[] sa : replacements) {
            if (sa[2].equals("reg")) { // regex
                Pattern pattern = (Pattern) sa[0];
                s = pattern.matcher(s).replaceAll((String) sa[1]);
            } else {
                s = s.replace((String) sa[0], (String) sa[1]);
            }

        }

        s = s.trim().replaceAll(" +", " "); // replace all spaces that have more than one space ("hello    world      " -> "hello world")
        s = shortenString(s, maxStringLength);

        printDebug("String Prepared", s);

        return s;
    }

    /**
     * Method to replace all unwanted characters in a String.
     *
     * @param string                the target String
     * @param maxStringLength       the maximum length of the string
     * @param removeAllSpecialChars is the string something a normal user (in the chatbot for example) has sent? if so we might want to handle it special and escape more/different stuff
     * @param toLowerCase           should the string be lower case?
     * @return the prepared String
     * @author Marc
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public static String prepareString(String string, int maxStringLength, boolean removeAllSpecialChars, boolean toLowerCase) {
        return prepareString(string, maxStringLength, removeAllSpecialChars, toLowerCase, true);
    }

    /**
     * Shortens a String to a defined max length
     *
     * @param s               the String which should be shortened
     * @param maxStringLength the max length for the String
     * @return the shortened String
     * @author Tim Irmler
     */
    public static String shortenString(String s, int maxStringLength) {
        if (stringTooLong(s, maxStringLength)) {
            printDebug("string length too big! trimming...", "s.length = " + s.length() + ", shortening to " + maxStringLength);
            s = s.substring(0, maxStringLength);
        }
        return s;
    }

    /**
     * checks if a String is too long
     *
     * @param s               the String which will be checked
     * @param maxStringLength the max length for the String
     * @return true if too long, false if smaller or equal.
     * @author Tim Irmler
     */
    public static boolean stringTooLong(String s, int maxStringLength) {
        return s.length() > maxStringLength;
    }
}
