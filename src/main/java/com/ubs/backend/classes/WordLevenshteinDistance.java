package com.ubs.backend.classes;

import com.ubs.backend.classes.database.Match;
import com.ubs.backend.classes.database.ResultParent;
import com.ubs.backend.classes.database.Tag;
import com.ubs.backend.classes.database.dao.MatchDAO;
import com.ubs.backend.util.CalculateRating;

import javax.persistence.EntityManager;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
public class WordLevenshteinDistance {
    private final String word;
    private final ResultParent resultParent;
    private final float levenshteinDistance;
    private Match match = null;

    public WordLevenshteinDistance(String word, ResultParent resultParent, float levenshteinDistance, float isCertainThreshold, EntityManager em) {
        this.word = word;
        this.resultParent = resultParent;
        this.levenshteinDistance = levenshteinDistance;

        printDebug("isCertainThreshold", isCertainThreshold);

        if (levenshteinDistance > -1) {
            if (levenshteinDistance < isCertainThreshold) { // add match
                Tag tag = resultParent.getTag();
                this.match = newMatch(tag, word, em);
            }
        }
    }

    /**
     * @param tag
     * @param w
     * @param em
     * @author Tim Irmler
     * @since 11.08.2021
     */
    private Match newMatch(Tag tag, String w, EntityManager em) {
        MatchDAO matchDAO = new MatchDAO();
        Match match = new Match(tag, w);
        printDebug("Match found", "Word(" + w + ") -> Tag(" + tag.getTag() + ")");
        return matchDAO.autoInsert(match, em);
    }

    public int[] getUpDownvotes() {
        if (match != null) {
            if (CalculateRating.isBadMatch(match.getUpvote(), match.getDownvote())) {
                return null;
            } else {
                return new int[]{match.getUpvote(), match.getDownvote()};
            }
        }
        return new int[]{resultParent.getUpvotes(), resultParent.getDownvotes()};
    }

    public String getWord() {
        return word;
    }

    public ResultParent getResultParent() {
        return resultParent;
    }

    public float getLevenshteinDistance() {
        return levenshteinDistance;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return "WordLevenshteinDistance{" +
                "word='" + word + '\'' +
                ", resultParent=" + resultParent +
                ", levenshteinDistance=" + levenshteinDistance +
                ", match=" + match +
                '}';
    }
}
