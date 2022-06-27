package com.ubs.backend.classes.database.dao.questions;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.questions.DefaultQuestion;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for com.ubs.backend.classes.database.dao.questions.DefaultQuestion
 *
 * @author Sarah Ambi
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class DefaultQuestionDAO extends DAO<DefaultQuestion> {
    public DefaultQuestionDAO() {
        super(DefaultQuestion.class);
    }

    /**
     * select random questions
     *
     * @param max number of default questions you want
     * @param em
     * @return the random questions in a arraylist
     */
    public List<DefaultQuestion> selectRandom(int max, EntityManager em) {
        Long count = (Long) em.createQuery("select count(ALL defaultQuestions) from DefaultQuestion defaultQuestions").getSingleResult();
        
        if (count < 1) {
            
        } else {
            int min = 0;
            ArrayList<DefaultQuestion> defaultQuestions = new ArrayList<>();
            ArrayList<Integer> usedIndexes = new ArrayList<>();
            
            for (int i = 0; i < max; i++) {
                int randomIndex;
                final int maxTries = 50;
                int tries = 1;
                do {
                    randomIndex = (int) ((Math.random() * (count - min)) + min);
                    
                    tries++;
                } while (usedIndexes.contains(randomIndex) && tries < maxTries);
                if (tries >= maxTries) {
                    
                    break;
                }
                
                usedIndexes.add(randomIndex);
                DefaultQuestion defaultQuestion = em.createQuery("select dq from DefaultQuestion dq", DefaultQuestion.class).setFirstResult(randomIndex).setMaxResults(1).getSingleResult();
                
                defaultQuestions.add(defaultQuestion);
            }
            return defaultQuestions;
        }
        return null;
    }

    public List<DefaultQuestion> selectRandom(int max) {
        EntityManager em = Connector.getInstance().open();

        List<DefaultQuestion> defaultQuestions = null;

        try {
            em.getTransaction().begin();
            defaultQuestions = selectRandom(max, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return defaultQuestions;
    }

    public Long count(EntityManager em) {
        return em.createQuery("select count(question) from DefaultQuestion question", Long.class).getSingleResult();
    }

    /**
     * Updates a defaultQuestion in the Database
     *
     * @param id       the id of the defaultquestion
     * @param question the new Content of the defaultquestion
     */
    public void update(long id, String question) {
        EntityManager em = Connector.getInstance().open();
        
        try {
            em.getTransaction().begin();
            DefaultQuestion defaultQuestion = select(id, em);
            defaultQuestion.setDefaultQuestion(question);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
