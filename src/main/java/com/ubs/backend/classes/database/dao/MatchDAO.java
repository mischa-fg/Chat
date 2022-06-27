package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Match;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionTimesResultDAO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * DAO for com.ubs.backend.classes.database.Match
 */
public class MatchDAO extends DAO<Match> {

    public MatchDAO() {
        super(Match.class);
    }

    /**
     * @param tagId
     * @param word
     * @param em
     * @return
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public Match selectByTagAndWord(long tagId, String word, EntityManager em) {
        try {
            return em.createQuery("select m from Match m where m.tag.tagID = :tagId and lower(m.word) = lower(:word)", Match.class).setParameter("tagId", tagId).setParameter("word", word).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Match selectByWord(String word, EntityManager em) {
        try {
            return em.createQuery("select m from Match m where lower(m.word) = lower(:word)", Match.class).setParameter("word", word).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Checks if a Match already exists and if it doesn't insert it into the Database
     *
     * @param trans the Match which is going to be checked
     * @param em    the EntityManager
     * @return how many matches there are
     * @author Magnus
     * @author Marc
     */
    public Match autoInsert(Match trans, EntityManager em) {
        Match matchDB = selectByTagAndWord(trans.getTag().getTagID(), trans.getWord(), em);
        if (matchDB == null) {
            printDebug("Match inserted", "Match(" + trans.getTag().getTag() + ", " + trans.getWord() + ")");
            matchDB = insert(trans, em);
        } else {
            printDebug("Match already exists in DB", "Match: " + matchDB);
        }
        return matchDB;
    }

    /**
     * @param tagId
     * @param em
     * @author Tim Irmler
     * @since 30.07.2021
     */
    public void removeByTag(long tagId, EntityManager em) {
        em.createQuery("delete from Match m where m.tag.tagID = :tagID").setParameter("tagID", tagId).executeUpdate();
    }

    /**
     * Votes a Tag
     *
     * @param matchID  the Tag which is being voted
     * @param answerID
     * @param isUpvote if the vote is positive
     * @param revert
     * @param question
     * @param em
     * @author Magnus
     * @author Marc
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void vote(Long matchID, Long answerID, boolean isUpvote, boolean revert, String question, EntityManager em) {
        try {
            Match t = select(matchID, em);

            if (t != null) {
                AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
                answeredQuestionTimesResultDAO.voteWithMatch(matchID, answerID, isUpvote, revert, question, em);

                if (isUpvote) {
                    t.setUpvote(t.getUpvote() + 1);
                    if (revert) {
                        t.setDownvote(((t.getDownvote() <= 0) ? 0 : t.getDownvote() - 1));
                    }
                } else {
                    t.setDownvote(t.getDownvote() + 1);
                    if (revert) {
                        t.setUpvote(((t.getUpvote() <= 0) ? 0 : t.getUpvote() - 1));
                    }
                }
            } else {
                printDebug("match = null", "match with id " + matchID + " does not exist or can't be found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Match resetRatings(long id, EntityManager em) {
        Match match = select(id, em);
        match.setDownvote(0);
        match.setUpvote(0);
        em.createQuery("update Match m set m.upvote = 0, m.downvote = 0 where m.matchID = :id").setParameter("id", id).executeUpdate();

        return match;
    }
}
