package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.TempAnswer;
import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.Result;
import com.ubs.backend.classes.database.UploadFile;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.UnansweredQuestionDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.UnansweredQuestion;
import com.ubs.backend.classes.database.statistik.AnswerStatistik;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.util.CalculateRating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * DAO for com.ubs.backend.classes.database.Answer
 *
 * @author Marc
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class AnswerDAO extends DAO<Answer> {

    /**
     * the default constructor
     */
    public AnswerDAO() {
        super(Answer.class);
    }

    /**
     * Adds a view to an answer
     *
     * @param answer the answer which should receive a view
     */
    public void view(Answer answer) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            view(answer, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * increases the view count of an answer
     *
     * @param answer the answer which should receive a view
     * @param em     the entity manager
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public void view(Answer answer, EntityManager em) {
        Answer a = select(answer.getAnswerID(), em);
        if (a != null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            StatistikTimes now = statistikTimesDAO.selectNow(true, em);
            AnswerStatistik answerStatistik = selectStatistikByTime(true, answer.getAnswerID(), now, em);
            if (answerStatistik != null) {
                answerStatistik.setAskedAmount(answerStatistik.getAskedAmount() + 1);
            }
        }
    }

    /**
     * selects the current {@link AnswerStatistik} of the specified {@link Answer} in the specified {@link StatistikTimes}.
     * If the answer has no Statistics for the specified {@link StatistikTimes}, the method can, if wanted, create it.
     *
     * @param createIfNotExists defines if an {@link AnswerStatistik} should be created if it does not exist.
     * @param answerID          the id of the {@link Answer} where we want to find a {@link AnswerStatistik}
     * @param statistikTimes    the specified time in which we want to have the statistics
     * @param em                the entity manager
     * @return the found {@link AnswerStatistik}. Returns null if nothing was found.
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public AnswerStatistik selectStatistikByTime(boolean createIfNotExists, long answerID, StatistikTimes statistikTimes, EntityManager em) {
        try {
            return em.createQuery("select aSt from AnswerStatistik aSt where aSt.statistikTimes.statistikID = :times and" +
                            " aSt.answer.answerID = :answerID", AnswerStatistik.class)
                    .setParameter("answerID", answerID)
                    .setParameter("times", statistikTimes.getStatistikID())
                    .getSingleResult();
        } catch (NoResultException e) {
            if (createIfNotExists) {
                Answer answer = select(answerID, em);
                if (answer != null) {
                    AnswerStatistik statistik = new AnswerStatistik(answer, statistikTimes);
                    em.persist(statistik);
                    return statistik;
                }
            }

            return null;
        }
    }

    /**
     * selects all {@link Result}s with a specific {@link com.ubs.backend.classes.database.Tag}
     *
     * @param id the id of the tag we're looking for
     * @param em the entity manager
     * @return a list of {@link Result}s containing the specified {@link com.ubs.backend.classes.database.Tag}
     */
    public List<Result> selectByTag(long id, EntityManager em) {
        try {
            return em.createQuery("select r from Result r where r.tag.tagID = :id", Result.class).setParameter("id", id).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Selects a list of {@link Answer}s containing random {@link Answer}s from the DB that all have a specific type, but without specifying an EntityManager.
     *
     * @param type the {@link AnswerType} of the Answer
     * @param max  defines how many {@link Answer}s the list can contain maximum
     * @return the list of {@link Answer}s
     * @author Tim Irmler
     * @see AnswerDAO#selectRandomByType(AnswerType, int, EntityManager) for Method with EntityManager.
     * @since 20.07.2021
     */
    public List<Answer> selectRandomByType(AnswerType type, int max) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<Answer> out = selectRandomByType(type, max, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * Selects a list of {@link Answer}s containing random {@link Answer}s from the DB that all have a specific type.
     *
     * @param type the {@link AnswerType} of the Answer
     * @param max  defines how many {@link Answer}s the list can contain maximum
     * @param em   the entity manager
     * @return the list of {@link Answer}s
     * @author Tim Irmler
     * @since 20.07.2021
     */
    public List<Answer> selectRandomByType(AnswerType type, int max, EntityManager em) {

        Long count = (Long) em.createQuery("select count(ALL answers) from Answer answers where answers.answerType = :type").setParameter("type", type).getSingleResult();

        if (count < 1) {
            return null;
        } else {
            List<Answer> allAnswers = selectByType(type, em);
            List<Answer> answers = new ArrayList<>();

            ArrayList<Integer> usedIndexes = new ArrayList<>();

            for (int i = 0; i < max; i++) {
                int randomIndex;
                int tries = 1;
                final int maxTries = 50;
                do {
                    randomIndex = (int) (Math.random() * allAnswers.size());

                    tries++;
                } while (usedIndexes.contains(randomIndex) && tries < maxTries);
                if (tries >= maxTries) {

                    break;
                }

                usedIndexes.add(randomIndex);
                Answer answer = allAnswers.get(randomIndex);

                answers.add(answer);
            }
            return answers;
        }
    }

    /**
     * creates a single {@link TempAnswer} from an {@link Answer} selected from the DB.
     *
     * @param answerID the id of the answer from which the {@link TempAnswer} will be created
     * @param em       the entity manager
     * @return the newly created {@link TempAnswer}
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public TempAnswer getSingleTempAnswer(long answerID, EntityManager em) {
        try {
            Answer answer = em.createQuery("SELECT a FROM Answer a WHERE a.answerID = :id", Answer.class).setParameter("id", answerID).getSingleResult();
            return new TempAnswer(answer);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * calculate average usefulness of an answer by looking at all of its connected tags and their rating.
     * We need to differentiate if the answer has grouped tags or not as we have to check different tables.
     *
     * @param id the id of the answer
     * @param em the entity manager
     * @return the average usefulness of an answer as a float
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public float getAverageUsefulness(long id, EntityManager em) {
        Answer answer = select(id, em);
        long upvotes = 0;
        long downvotes = 0;

        if (answer.getAnswerType().isGroupedTags()) {
            Long count = (Long) em.createQuery("select count(tt) from TypeTag tt where tt.answerType = :type").setParameter("type", answer.getAnswerType()).getSingleResult();

            if (count > 0) {
                upvotes = em.createQuery("select sum(r.upvotes) from TypeTag r where r.answerType = :type", Long.class).setParameter("type", answer.getAnswerType()).getSingleResult();
                downvotes = em.createQuery("select sum(r.downvotes) from TypeTag r where r.answerType = :type", Long.class).setParameter("type", answer.getAnswerType()).getSingleResult();
            }
        } else {
            Long count = (Long) em.createQuery("select count(r) from Result r where r.answer.answerID = :id").setParameter("id", answer.getAnswerID()).getSingleResult();

            if (count > 0) {
                upvotes = em.createQuery("select sum(r.upvotes) from Result r where r.answer.answerID = :id", Long.class).setParameter("id", id).getSingleResult();
                downvotes = em.createQuery("select sum(r.downvotes) from Result r where r.answer.answerID = :id", Long.class).setParameter("id", id).getSingleResult();
            }
        }

        return CalculateRating.getRating(upvotes, downvotes);
    }

    /**
     * Method to select a List of every {@link Answer} with a certain type and tag
     *
     * @param tagID the id of the tag we're looking for
     * @param type  the type of the answer we're looking for
     * @param em    the entity manager
     * @return a List of every {@link Answer} with a certain type and tag
     * @author Tim Irmler
     * @since 28.07.2021
     */
    public List<Answer> selectByTypeAndTag(long tagID, AnswerType type, EntityManager em) {
        printDebug("New Method", "AnswerDAO.selectByTypeAndTag");
        printDebug("method parameters", "tagID = " + tagID + ", type = " + type);
        try {
            if (type.isGroupedTags()) {

                return em.createQuery("select a from Answer a, TypeTag tt where a.answerType = :type and (tt.tag.tagID = :tagID and tt.answerType = :type)", Answer.class).setParameter("type", type).setParameter("tagID", tagID).getResultList();
            } else {

                return em.createQuery("select a from Answer a, Result r where a.answerType = :type and (r.answer.answerID = a.answerID and r.tag.tagID = :tagID)", Answer.class).setParameter("type", type).setParameter("tagID", tagID).getResultList();
            }
        } catch (NoResultException e) {

            return null;
        }
    }

    /**
     * Select all answers with a set Type with a preset EntityManager
     *
     * @param type the type which we want to answers to contain
     * @param em   the EntityManager which we will use
     * @return a list of all answers with the set type
     */
    public List<Answer> selectByType(AnswerType type, EntityManager em) {
        return em.createQuery("select a from Answer a where a.answerType = :type", Answer.class).setParameter("type", type).getResultList();
    }

    /**
     * Select all answers with a set Type
     *
     * @param type the type which we want to answers to contain
     * @return a list of all answers with the set type
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Answer> selectByType(AnswerType type) {
        EntityManager em = Connector.getInstance().open();
        List<Answer> out = null;

        try {
            em.getTransaction().begin();

            out = selectByType(type, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return out;
    }

    /**
     * Count how many answers there are per type
     *
     * @param type the type we want to know how many answers have
     * @param em   the entity manager
     * @return the amount of answers as an int
     * @author Tim Irmler
     */
    public int countByType(AnswerType type, EntityManager em) {
        Long count = (Long) em.createQuery("select count(answer) from Answer answer where answer.answerType = :type").setParameter("type", type).getSingleResult();

        return count.intValue();
    }

    /**
     * counts how many answers there are per type and tag
     *
     * @param tagID the id of the tag we want to look for
     * @param type  the type we want to look for
     * @param em    the entity manager
     * @return the amount of answers per type and tag as int
     * @author Tim Irmler
     * @since 27.07.2021
     */
    public int countByTypeAndTag(long tagID, AnswerType type, EntityManager em) {
        Long count;
        if (type.isGroupedTags()) {
            count = (Long) em.createQuery("select count(answer) from Answer answer, TypeTag tt where answer.answerType = :type and (tt.tag.tagID = :tagID and tt.answerType = :type)").setParameter("type", type).setParameter("tagID", tagID).getSingleResult();

        } else {
            count = (Long) em.createQuery("select count(answer) from Answer answer, Result r where answer.answerType = :type and (r.tag.tagID = :id and r.answer.answerID = answer.answerID)").setParameter("type", type).setParameter("id", tagID).getSingleResult();

        }
        return count.intValue();
    }

    /**
     * Inserts an answer into the Database
     *
     * @param answer the {@link Answer} which should be stored!
     */
    public Answer insert(Answer answer) {
        EntityManager em = Connector.getInstance().open();

        Answer a = null;
        try {
            em.getTransaction().begin();

            a = insert(answer, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return a;
    }

    /**
     * Removes an object from the database by id
     *
     * @param id the id of the object which is going to be removed
     */
    public void remove(long id) {
        EntityManager em = Connector.getInstance().open();
        try {
            em.getTransaction().begin();

            remove(id, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * removes an {@link Answer} from the db, including its results and files. {@link AnsweredQuestion}s that would return this answer will be marked as {@link UnansweredQuestion}
     *
     * @param id the id of the answer to delete
     * @param em the entity manager
     */
    public void remove(long id, EntityManager em) {
        Answer answer = select(id, em);

        if (answer != null) {
            if (answer.getAnswerType().isGroupedTags()) {
                // nothing as there are no answered questions, results or anything (at least not rn) connected to this specific answer
            } else {
                ResultDAO resultDAO = new ResultDAO();
                resultDAO.removeByAnswer(answer.getAnswerID(), em);

                // get all answered questions without any results
                AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
                List<AnsweredQuestion> answeredQuestions = answeredQuestionDAO.selectWithoutResults(em);

                for (AnsweredQuestion answeredQuestion : answeredQuestions) {

                    answeredQuestionDAO.doRemove(answeredQuestion, em);

                    // set the question as unanswered
                    UnansweredQuestionDAO unansweredQuestionDAO = new UnansweredQuestionDAO();
                    if (unansweredQuestionDAO.selectByQuestion(answeredQuestion.getQuestion(), em) == null) {
                        unansweredQuestionDAO.insert(new UnansweredQuestion(answeredQuestion.getQuestion()), em);
                    }
                }
            }

            answer.setFiles(null);

            em.createQuery("delete from AnswerStatistik aSt where aSt.answer.answerID = :id").setParameter("id", answer.getAnswerID()).executeUpdate();

            em.createQuery("delete from Answer a where a.answerID = :id").setParameter("id", answer.getAnswerID()).executeUpdate();
        }
    }

    /**
     * Sets the files of a question
     *
     * @param answerID the ID of the answer
     * @param fileID   the ID of the file
     */
    public void setFile(long answerID, long fileID) {
        UploadFileDAO fileDAO = new UploadFileDAO();

        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            UploadFile file = fileDAO.select(fileID, em);
            Answer a = select(answerID, em);

            a.setFiles(new ArrayList<>(Collections.singleton(file)));

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * counts how often an answer has been sent, but without providing an EntityManager.
     * {@link AnswerDAO#countAskedAmount(long, EntityManager)} will be called with the newly created EntityManager
     *
     * @param answerID the id of the answer to count how often it has been sent
     * @return the amount of how often the answer has been sent as a Long.
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public Long countAskedAmount(long answerID) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        Long askedAmount = countAskedAmount(answerID, em);

        em.getTransaction().commit();
        em.close();
        return askedAmount;
    }

    /**
     * counts how often an answer has been sent, while providing an EntityManager.
     *
     * @param answerID the id of the answer
     * @param em       the EntityManager
     * @return the amount of how often the answer has been sent as a Long.
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public Long countAskedAmount(long answerID, EntityManager em) {
        try {
            return em.createQuery("select sum(aSt.askedAmount) from AnswerStatistik aSt where aSt.answer.answerID = :answerID" +
                    " group by aSt.answer", Long.class).setParameter("answerID", answerID).getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
}
