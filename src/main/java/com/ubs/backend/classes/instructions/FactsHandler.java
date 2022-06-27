package com.ubs.backend.classes.instructions;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.AnswerDAO;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
public class FactsHandler implements InstructionHandler {
    @Override
    public Answer handle(Answer answer) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO dao = new AnswerDAO();
        int count = dao.countByType(AnswerType.FACTS, em);

        if (count > 0) {
            List<Answer> facts = dao.selectRandomByType(AnswerType.FACTS, 1, em);

            em.getTransaction().commit();
            em.close();

            return facts.get(0);
        } else {
            em.getTransaction().commit();
            em.close();
            return null;
        }
    }

    @Override
    public String toString() {
        return "FactsHandler{}";
    }
}
