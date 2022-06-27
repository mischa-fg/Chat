package com.ubs.backend.classes.database.dao.questions;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.dao.statistik.UnansweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.questions.UnansweredQuestion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * DAO for com.ubs.backend.classes.database.question.UnansweredQuestion
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class UnansweredQuestionDAO extends DAO<UnansweredQuestion> {

    public UnansweredQuestionDAO() {
        super(UnansweredQuestion.class);
    }

    public UnansweredQuestion selectByQuestion(String question, EntityManager em) {
        try {
            return em.createQuery("select uQ from UnansweredQuestion uQ where lower(uQ.question) = lower(:question)", UnansweredQuestion.class).setParameter("question", question).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UnansweredQuestion selectByQuestion(String question) {
        EntityManager em = Connector.getInstance().open();
        UnansweredQuestion unansweredQuestion = null;

        try {
            em.getTransaction().begin();
            unansweredQuestion = selectByQuestion(question, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return unansweredQuestion;
    }

    /**
     * Removes an UnansweredQuestion in the Database by it's ID
     *
     * @param id the id of the object which is going to be removed
     */
    @Override
    public void remove(long id) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            em.createQuery("delete from UnansweredQuestion q where q.unansweredQuestionID = :id").setParameter("id", id).executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void remove(UnansweredQuestion unansweredQuestion) {
        EntityManager em = Connector.getInstance().open();

        try {
            doRemove(unansweredQuestion, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void remove(long id, EntityManager em) {
        removeAllReferences(id, em);

        em.createQuery("delete from UnansweredQuestion aQ where aQ.unansweredQuestionID = :id").setParameter("id", id).executeUpdate();
    }

    public boolean doRemove(UnansweredQuestion unansweredQuestion, EntityManager em) {
        try {
            boolean b = removeAllReferences(unansweredQuestion.getUnansweredQuestionID(), em);

            em.remove(unansweredQuestion);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeAllReferences(long id, EntityManager em) {
        try {
            UnansweredQuestionStatistikDAO unansweredQuestionStatistikDAO = new UnansweredQuestionStatistikDAO();
            return unansweredQuestionStatistikDAO.removeByQuestion(id, em);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeByQuestion(String question, EntityManager em) {
        UnansweredQuestion unansweredQuestion = selectByQuestion(question, em);
        if (unansweredQuestion != null) {
            return doRemove(unansweredQuestion, em);
        } else {
            
            return false;
        }
    }
}