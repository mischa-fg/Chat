package com.ubs.backend.util;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class to calculate the Levenshtein distance between two words
 *
 * @author Marc
 * @author Magnus
 * @since 17.07.2021
 */
public class Levenshtein {
    /**
     * Function for checking the cost of changing a character in the string
     *
     * @param a current character
     * @param b replacement character
     * @return cost of changing the character
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    /**
     * Minimal number in array
     *
     * @param numbers all numbers which we want to check
     * @return the lowest number from the numbers given
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    /**
     * Function to find the Levenshtein distance using a table approach
     *
     * @param x             the string which is used to compare
     * @param y             the string which is used as a comparison
     * @param caseSensitive is it case sensitive or not?
     * @return the minimal amount of changes made to a string for it to be equal to the second one
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public static int calculate(String x, String y, boolean caseSensitive) {
        x = rsc(x);
        y = rsc(y);
        if (!caseSensitive) {
            x = x.toLowerCase(Locale.ROOT);
            y = y.toLowerCase(Locale.ROOT);
        }
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    /**
     * @param s
     * @return
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    private static String rsc(String s) {
        char[] ca = s.toCharArray();

        for (int i = 0; i < ca.length; i++) {
            char c = ca[i];
            switch (c) {
                case 228: // ä
                    c = 'a';
                    break;
                case 246: // ö
                    c = 'o';
                    break;
                case 252: // ü
                    c = 'u';
                    break;
            }
            ca[i] = c;
        }

        return new String(ca);
    }
}
