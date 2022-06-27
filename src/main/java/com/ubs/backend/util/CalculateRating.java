package com.ubs.backend.util;

import com.ubs.backend.classes.database.Match;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
public class CalculateRating {
    final public static int minDownvotesForNoTranslate = 3;
    final private static float minOffsetForNoTranslate = 1.1f;


    /**
     * Calculate the amount of downvotes which are needed to mark a {@code Match} as bad
     *
     * @param upvotes the amount of upvotes the Match has
     * @return the amount of Downvotes needed
     * @see Match
     */
    public static float getOffset(int upvotes) {
        return (upvotes + minDownvotesForNoTranslate) * minOffsetForNoTranslate;
    }

    /**
     * Checks if a {@code Match} is considered bad based on the amount of Upvotes and Downvotes it received.
     *
     * @param upvotes   the amount of upvotes this Match received
     * @param downvotes the amount of downvotes this Match received
     * @return if a Match is bad
     * @see Match
     */
    public static boolean isBadMatch(int upvotes, int downvotes) {
        // if downvotes is at least 3, or 1.25 more then the upvotes, return true because its a bad match

        final float upvotesTimesOffset = getOffset(upvotes);
        printDebug("upvotes/downvotes of match", upvotes + "/" + downvotes);
        printDebug("downvotes needed for bad match if upvotes > 0", upvotesTimesOffset);
        boolean b = false;
        if (downvotes != 0) {
            if (upvotes <= 0 && downvotes >= minDownvotesForNoTranslate) {
                printDebug("upvotes are 0 and downvotes are at least " + minDownvotesForNoTranslate, "");
                b = true;
            } else if (downvotes >= upvotesTimesOffset) {
                b = true;
            }
        }
        if (b) {
            printDebug("FOUND BAD MATCH!", "SHOULD BE IGNORED!");
        } else {
            printDebug("not bad match", "no ignore");
        }
        return b;
    }

    /**
     * Returns if a {@code Match} is Translated as a String
     * Used in the Frontend to visualize it to the admins
     *
     * @param match The Match which will be checked
     * @return the String mentioned above in German
     * @see Match
     */
    public static String getMatchStatus(Match match) {
        printDebug("getting match status", match);
        String status;
        if (CalculateRating.isBadMatch(match.getUpvote(), match.getDownvote())) {
            status = "Wird nicht übersetzt";
        } else {
            status = "Wird übersetzt";
        }
        return status;
    }

    /**
     * Converts the Levenshtein Distance to a useful value between 0 and 1
     * a smaller number is better
     *
     * @param distanceRaw            the raw Levenshtein distance
     * @param stringLengthDifference the length of the longer {@code String} of the two which were compared
     * @return useful value between 0 and 1
     */
    public static float getLevenshteinDistance(int distanceRaw, int stringLengthDifference) {
        return 1 - (float) distanceRaw / (float) stringLengthDifference;
    }

    /**
     * Get the Rating for a Translation to check if it is good or bad
     *
     * @param upvotes             the amount of Upvotes the {@code Match} has
     * @param downvotes           the amount of Downvotes the {@code Match} has
     * @param levenstheinDistance the levenshtein Distance of the two Words
     * @return the Rating for this Match
     */
    public static float getRating(int upvotes, int downvotes, float levenstheinDistance) {
        if (levenstheinDistance <= 0) {
            return getRating(upvotes, downvotes);
        } else {
            float rating = getRating(upvotes, downvotes) * levenstheinDistance;
            printDebug("rating after * " + levenstheinDistance + " (distance)", rating);
            return rating;
        }
    }

    /**
     * Get the Rating for a Translation to check if it is good or bad
     *
     * @param upvotes   the amount of Upvotes the {@code Match} has
     * @param downvotes the amount of Downvotes the {@code Match} has
     * @return the Rating for this Match
     */
    public static float getRating(int upvotes, int downvotes) {
        int total = upvotes + downvotes;
        float rating;
        if (total > 0) {
            rating = (float) upvotes / total;
            if (rating == 0) {
                rating = 0f;
            }
            printDebug("rating calculation", upvotes + " / (" + upvotes + " + " + downvotes + ") = " + rating);
        } else {
            rating = 0.5f;
            printDebug("rating because total is 0", rating);
        }
        return rating;
    }

    /**
     * Get the Rating for a Translation to check if it is good or bad
     *
     * @param upvotes   the amount of Upvotes the {@code Match} has
     * @param downvotes the amount of Downvotes the {@code Match} has
     * @return the Rating for this Match
     */
    public static float getRating(long upvotes, long downvotes) {
        return getRating((int) upvotes, (int) downvotes);
    }
}
