package com.ubs.backend.classes.database.dao.questions;

import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.*;
import com.ubs.backend.classes.database.dao.statistik.AnsweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.AnsweredQuestionResult;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * DAO for com.ubs.backend.classes.database.question.AnsweredQuestion
 *
 * @author Sarah
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class AnsweredQuestionDAO extends DAO<AnsweredQuestion> {
    public AnsweredQuestionDAO() {
        super(AnsweredQuestion.class);
    }

    public AnsweredQuestion selectByQuestion(String question, EntityManager em) {
        try {
            return em.createQuery("select aQ from AnsweredQuestion aQ where lower(aQ.question) = lower(:question)", AnsweredQuestion.class).setParameter("question", question).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Selects an {@link AnsweredQuestion} from the Database which answered the specified Question
     *
     * @param question the Question, which the {@link AnsweredQuestion} should answer
     * @return the {@link AnsweredQuestion} from the Database
     */
    public AnsweredQuestion selectByQuestion(String question) {
        EntityManager em = Connector.getInstance().open();
        AnsweredQuestion answeredQuestions = null;

        try {
            em.getTransaction().begin();

            answeredQuestions = selectByQuestion(question, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestions;
    }

    /**
     * Selects a {@link Result} from the Database from a {@link Match}, {@link Answer} and a Question
     *
     * @param matchID  the ID of the {@link Match} which will be used to search
     * @param answerID the ID of the {@link Answer} which will be used to search
     * @param question the Question, which the Result is for
     * @param em       the EntityManager which will be used
     * @return {@link AnsweredQuestionResult} which has the specified attributes
     * @author Tim Irmler
     * @since 21.08.2021
     */
    public AnsweredQuestionResult selectResultByQuestionAndMatchAndAnswer(long matchID, long answerID, String question, EntityManager em) {
        AnsweredQuestion answeredQuestion = selectByQuestion(question, em);
        if (answeredQuestion != null) {
            MatchDAO matchDAO = new MatchDAO();
            Match match = matchDAO.select(matchID, em);
            if (match != null) {
                AnswerDAO answerDAO = new AnswerDAO();
                Answer answer = answerDAO.select(answerID, em);
                if (answer != null) {
                    if (!answer.isHidden()) {
                        if (!answer.getAnswerType().isGroupedTags()) {
                            ResultDAO resultDAO = new ResultDAO();
                            Result result = resultDAO.selectByTagAndAnswer(answerID, match.getTag().getTagID(), em);
                            return selectResultByResultAndAnsweredQuestionId(result.getId(), answeredQuestion.getAnsweredQuestionID(), em);
                        } else {
                            TypeTagDAO typeTagDAO = new TypeTagDAO();
                            TypeTag typeTag = typeTagDAO.selectByTagAndType(answer.getAnswerType(), match.getTag().getTagID(), em);
                            return selectResultByResultAndAnsweredQuestionId(typeTag.getId(), answeredQuestion.getAnsweredQuestionID(), em);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * returns a list containing all answered questions without a result. which means, that the question can no longer be answered
     *
     * @param em the entity manager
     * @return a list containing answered questions without result
     */
    public List<AnsweredQuestion> selectWithoutResults(EntityManager em) {
        try {
            return em.createQuery("select aQ from AnsweredQuestion aQ where aQ.answeredQuestionID not in (select aQR.answeredQuestion.answeredQuestionID from AnsweredQuestionResult aQR)", AnsweredQuestion.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param resultParent
     * @param em
     * @return
     * @author Tim Irmler
     * @since 01.08.2021
     */
    public List<AnsweredQuestionResult> selectResultByResult(ResultParent resultParent, EntityManager em) {
        return selectResultByResult(resultParent.getId(), em);
    }

    public List<AnsweredQuestionResult> selectResultByResult(long resultID, EntityManager em) {
        try {
            return em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.result.id = :resultID or aqr.typeTag.id = :resultID", AnsweredQuestionResult.class).setParameter("resultID", resultID).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param resultParent
     * @param question
     * @param em
     * @return
     * @author Tim Irmler
     * @since 01.08.2021
     */
    public List<AnsweredQuestionResult> selectByResultAndQuestion(ResultParent resultParent, String question, EntityManager em) {
        AnsweredQuestion answeredQuestion = selectByQuestion(question, em);

        if (resultParent instanceof Result) {
            return em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.result.id = :resultID and aqr.answeredQuestion.answeredQuestionID = :questionID", AnsweredQuestionResult.class).setParameter("resultID", resultParent.getId()).setParameter("questionID", answeredQuestion.getAnsweredQuestionID()).getResultList();
        } else if (resultParent instanceof TypeTag) {
            return em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.typeTag.id = :typeTagID and aqr.answeredQuestion.answeredQuestionID = :questionID", AnsweredQuestionResult.class).setParameter("typeTagID", resultParent.getId()).setParameter("questionID", answeredQuestion.getAnsweredQuestionID()).getResultList();
        }
        return null;
    }

    public List<AnsweredQuestionResult> selectResultByAnsweredQuestionId(long id) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<AnsweredQuestionResult> out = selectResultByAnsweredQuestionId(id, em);

        em.getTransaction().commit();
        em.close();
        return out;
    }

    public List<AnsweredQuestionResult> selectResultByAnsweredQuestionId(long id, EntityManager em) {
        return em.createQuery("select r from AnsweredQuestionResult r where r.answeredQuestion.answeredQuestionID = :id", AnsweredQuestionResult.class).setParameter("id", id).getResultList();
    }

    /**
     * @param resultID
     * @param questionID
     * @param em
     * @return
     * @author Tim Irmler
     * @since 04.08.2021
     */
    public AnsweredQuestionResult selectResultByResultAndAnsweredQuestionId(long resultID, long questionID, EntityManager em) {
        return em.createQuery("select r from AnsweredQuestionResult r where (r.result.id = :resultID or r.typeTag.id = :resultID) and r.answeredQuestion.answeredQuestionID = :questionID", AnsweredQuestionResult.class)
                .setParameter("resultID", resultID).setParameter("questionID", questionID).getSingleResult();
    }

    /**
     * @param tagId
     * @param type
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.07.2021
     */
    public List<AnsweredQuestionResult> selectResultByTagAndType(long tagId, AnswerType type, EntityManager em) {
        try {
            if (type.isGroupedTags()) {
                TypeTagDAO typeTagDAO = new TypeTagDAO();
                TypeTag typeTag = typeTagDAO.selectByTagAndType(type, tagId, em);
                return em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.typeTag.id = :id", AnsweredQuestionResult.class).setParameter("id", typeTag.getId()).getResultList();
            } else {
                ResultDAO resultDAO = new ResultDAO();
                List<Result> results = resultDAO.selectByTagAndType(tagId, type, em);
                List<AnsweredQuestionResult> answeredQuestionResults = new ArrayList<>();

                for (Result result : results) {
                    List<AnsweredQuestionResult> answeredQuestionResultsDB = em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.result.id = :id", AnsweredQuestionResult.class).setParameter("id", result.getId()).getResultList();
                    answeredQuestionResults.addAll(answeredQuestionResultsDB);
                }

                return answeredQuestionResults;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void remove(long id, EntityManager em) {
        removeAllReferences(id, em);
        em.createQuery("delete from AnsweredQuestion aQ where aQ.answeredQuestionID = :id").setParameter("id", id).executeUpdate();
    }

    public boolean removeByQuestion(String question, EntityManager em) {
        AnsweredQuestion answeredQuestion = selectByQuestion(question, em);
        if (answeredQuestion != null) {
            return doRemove(answeredQuestion, em);
        } else {
            return false;
        }
    }

    /**
     * @param id the id of the answeredQuestion
     * @param em
     * @return
     * @author Tim Irmler
     */
    public boolean removeAllReferences(long id, EntityManager em) {
        try {
            AnsweredQuestionStatistikDAO answeredQuestionStatistikDAO = new AnsweredQuestionStatistikDAO();

            return (removeResultsByAnswer(id, em) && answeredQuestionStatistikDAO.removeByQuestion(id, em));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doRemove(AnsweredQuestion answeredQuestion, EntityManager em) {
        try {
            boolean b = removeAllReferences(answeredQuestion.getAnsweredQuestionID(), em);

            em.remove(answeredQuestion);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeResultByResult(ResultParent resultParent) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        if (resultParent instanceof Result) {
            removeResultByResult(resultParent.getId(), em);
        } else if (resultParent instanceof TypeTag) {
            removeResultByTypeTag(resultParent.getId(), em);
        }


        em.getTransaction().commit();
        em.close();
    }

    public void removeResultByResult(long id, EntityManager em) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        answeredQuestionTimesResultDAO.removeByResult(id, em);

        em.createQuery("delete from AnsweredQuestionResult aQR where aQR.result.id = :id").setParameter("id", id).executeUpdate();
    }

    public void removeResultByTypeTag(long id, EntityManager em) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        answeredQuestionTimesResultDAO.removeByResult(id, em);

        em.createQuery("delete from AnsweredQuestionResult aqr where aqr.typeTag.id = :id").setParameter("id", id).executeUpdate();
    }

    /**
     * @param tagId
     * @param em
     * @author Tim Irmler
     * @since 29.07.2021
     */
    public void removeResultByTag(long tagId, EntityManager em) {
        // remove results
        ResultDAO resultDAO = new ResultDAO();
        List<Result> results = resultDAO.selectByTag(tagId);
        for (Result result : results) {
            em.createQuery("delete from AnsweredQuestionResult aqr where aqr.result.id = :resultID").setParameter("resultID", result.getId()).executeUpdate();
        }
        // remove types
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        List<TypeTag> typeTags = typeTagDAO.selectByTag(tagId, em);
        for (TypeTag typeTag : typeTags) {
            em.createQuery("delete from AnsweredQuestionResult aqr where aqr.typeTag.id = :typeTagID").setParameter("typeTagID", typeTag.getId()).executeUpdate();
        }
    }

    /**
     * @param tagId
     * @param answerType
     * @param em
     * @author Tim Irmler
     * @since 29.07.2021
     */
    public void removeResultByTagAndType(long tagId, AnswerType answerType, EntityManager em) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        if (answerType.isGroupedTags()) {
            List<AnsweredQuestionResult> answeredQuestionResults = selectResultByTagAndType(tagId, answerType, em);
            for (AnsweredQuestionResult answeredQuestionResult : answeredQuestionResults) {
                answeredQuestionTimesResultDAO.removeByResult(answeredQuestionResult.getResultID(), em);
                em.createQuery("delete from AnsweredQuestionResult aqr where aqr.resultID = :resultID").setParameter("resultID", answeredQuestionResult.getResultID()).executeUpdate();
            }
        } else {
            List<AnsweredQuestionResult> answeredQuestionResults = selectResultByTagAndType(tagId, answerType, em);
            for (AnsweredQuestionResult answeredQuestionResult : answeredQuestionResults) {
                answeredQuestionTimesResultDAO.removeByResult(answeredQuestionResult.getResultID(), em);
                em.createQuery("delete from AnsweredQuestionResult aqr where aqr.resultID = :resultID").setParameter("resultID", answeredQuestionResult.getResultID()).executeUpdate();
            }

        }
    }

    /**
     * @param id the answered question id
     * @param em
     */
    public boolean removeResultsByAnswer(long id, EntityManager em) {
        try {
            printDebug("remove results by answer(ed question)", "answered question id = " + id);
            AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
            answeredQuestionTimesResultDAO.removeByAnsweredQuestion(id, em);
            em.createQuery("delete from AnsweredQuestionResult aqr where aqr.answeredQuestion.answeredQuestionID = :id").setParameter("id", id).executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}