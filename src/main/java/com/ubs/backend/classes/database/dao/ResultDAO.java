package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.Result;
import com.ubs.backend.classes.database.ResultParent;
import com.ubs.backend.classes.database.TypeTag;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionTimesResultDAO;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * DAO for com.ubs.backend.classes.database.Result
 *
 * @author Marc
 * @author Magnus
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class ResultDAO extends DAO<Result> {

    public ResultDAO() {
        super(Result.class);
    }

    /**
     * Select by answer list.
     *
     * @param id  the id
     * @param max the max
     * @param em  the em
     * @return list
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByAnswer(long id, int max, EntityManager em) {
        try {
            if (max < 0) {
                // get all results
                return selectByAnswer(id, em);
            }
            // only get as many results as defined
            return em.createQuery("select aR from Result aR where aR.answer.answerID = :id", Result.class).setParameter("id", id).setMaxResults(max).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Select by answer list.
     *
     * @param id the id
     * @param em the em
     * @return list
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByAnswer(long id, EntityManager em) {
        try {
            return em.createQuery("select aR from Result aR where aR.answer.answerID = :id", Result.class).setParameter("id", id).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Select by answer list.
     *
     * @param id the id
     * @return list
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByAnswer(long id) {
        EntityManager em = Connector.getInstance().open();
        List<Result> results = null;

        try {
            em.getTransaction().begin();

            results = selectByAnswer(id, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return results;
    }

    /**
     * Select by type list.
     *
     * @param type the type
     * @param em   the em
     * @return list
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByType(AnswerType type, EntityManager em) {
        try {
            return em.createQuery("select aR from Result aR where aR.answer.answerType = :type", Result.class).setParameter("type", type).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * selects all result sets that have the specified answer, with the specified type
     *
     * @param id   the id of the answer
     * @param type the answer type
     * @param em   the entity manager
     * @return all result sets that have the specified answer with the specified type
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByAnswerAndType(long id, AnswerType type, EntityManager em) {
        try {
            return em.createQuery("select aR from Result aR where aR.answer.answerID = :id and aR.answer.answerType = :type", Result.class).setParameter("id", id).setParameter("type", type).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * selects a single result, that has the specified answer and tag
     *
     * @param answerId the id of the answer
     * @param tagId    the id of the tag
     * @param em       the entity manager
     * @return selects a single result, that has the specified answer and tag
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Result selectByTagAndAnswer(long answerId, long tagId, EntityManager em) {
        try {
            return em.createQuery("select r from Result r where r.answer.answerID = :aID and r.tag.tagID = :tID", Result.class).setParameter("aID", answerId).setParameter("tID", tagId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Result> selectByTagAndType(long tagId, AnswerType type, EntityManager em) {
        if (type.isGroupedTags()) {
            return null;
        }
        try {
            return em.createQuery("select r from Result r where r.tag.tagID = :id and r.answer.answerType = :type", Result.class).setParameter("id", tagId).setParameter("type", type).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * get all results that have the specified tag
     *
     * @param id the id of the tag
     * @param em the entity manager
     * @return all results that have the specified tag
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByTag(long id, EntityManager em) {
        try {
            return em.createQuery("select r from Result r where r.tag.tagID = :id", Result.class).setParameter("id", id).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param id
     * @return
     * @author TIm Irmler
     * @since 17.07.2021
     */
    public List<Result> selectByTag(long id) {
        EntityManager em = Connector.getInstance().open();
        List<Result> results = null;

        try {
            em.getTransaction().begin();

            results = selectByTag(id, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return results;
    }

    /**
     * Adds a usage to a Result
     *
     * @param result the result which should receive a usage
     * @author Marc
     * @since 17.07.2021
     */
    public void view(ResultParent result) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            if (result instanceof Result) {
                Result r = em.createQuery("select r from Result r where r.id = :id", Result.class).setParameter("id", result.getId()).getSingleResult();
                r.setUsages(r.getUsages() + 1);
            } else if (result instanceof TypeTag) {
                TypeTag t = em.createQuery("select t from TypeTag t where t.id = :id", TypeTag.class).setParameter("id", result.getId()).getSingleResult();
                t.setUsages(t.getUsages() + 1);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * @param id
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void removeByAnswer(long id) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            removeByAnswer(id, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * removes all results with specified answer
     *
     * @param id the id of the answer
     * @param em the EntityManager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void removeByAnswer(long id, EntityManager em) {
        List<Result> results = selectByAnswer(id, em);


        // delete every answeredQuestionResult with this result, containing this answer
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        for (Result result : results) {
            answeredQuestionTimesResultDAO.removeByResult(result.getId(), em);
            answeredQuestionDAO.removeResultByResult(result.getId(), em);
        }

        em.createQuery("delete from Result r where r.answer.answerID = :id").setParameter("id", id).executeUpdate();
    }

    /**
     * Removes a tag from an answer
     *
     * @param answerID the ID of the answer
     * @param tagID    the ID of the tag
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void removeTagFromAnswer(long answerID, long tagID) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            removeTagFromAnswer(answerID, tagID, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * removes result set that has the specified answer and tag
     *
     * @param answerID the id of the answer
     * @param tagID    the id of the tag
     * @param em       the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void removeTagFromAnswer(long answerID, long tagID, EntityManager em) {
        Result result = selectByTagAndAnswer(answerID, tagID, em);

        // delete every answeredQuestionResult with this result, containing this answer
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();

        answeredQuestionDAO.removeResultByResult(result.getId(), em);
        em.createQuery("delete from Result r where r.answer.answerID = :answerID and r.tag.tagID = :tagID").setParameter("answerID", answerID).setParameter("tagID", tagID).executeUpdate();
    }

    /**
     * @param id
     * @param em
     * @author Tim Irmler
     * @since 19.07.2021
     */
    public void removeTag(long id, EntityManager em) {
        List<Result> results = selectByTag(id, em);
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        for (Result result : results) {
            answeredQuestionTimesResultDAO.removeByResult(result.getId(), em);

            answeredQuestionDAO.removeResultByResult(result.getId(), em);
        }

        em.createQuery("delete from Result r where r.tag.tagID = :id").setParameter("id", id).executeUpdate();
    }

    /**
     * Votes a Result
     *
     * @param answerID the ID of the Answer
     * @param tagID    the ID of the Tag
     * @param isUpvote if the vote is positive
     * @author Marc
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void vote(long answerID, Long tagID, boolean isUpvote, boolean revert, String question, EntityManager em) {
        try {
            Result r = selectByTagAndAnswer(answerID, tagID, em);
            if (r != null) {
                if (!r.getAnswer().isHidden()) {
                    AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
                    answeredQuestionTimesResultDAO.vote(r, isUpvote, revert, question, em);
                }

                if (isUpvote) {
                    r.setUpvotes(r.getUpvotes() + 1);
                    if (revert) {
                        r.setDownvotes(r.getDownvotes() - 1);
                    }
                } else {
                    r.setDownvotes(r.getDownvotes() + 1);
                    if (revert) {
                        r.setUpvotes(r.getUpvotes() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
