package com.ubs.backend.classes.instructions.Statistics;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.statistik.CreationOfDB;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;

/**
 * @author Tim Irmler
 * @since 13.08.2021
 */
public class DBActiveSince implements Statistic {

    /**
     * Gets the DB Creation Date from the Database
     *
     * @param em the EntityManager to use
     * @return an {@link Answer} containing amount of time this Chatbot has been running
     */
    @Override
    public Answer getFact(EntityManager em) {
        CreationOfDB creationOfDB = em.createQuery("select cdb from CreationOfDB cdb", CreationOfDB.class).setMaxResults(1).getSingleResult();
        String time = creationOfDB.getStatistikTimes().getFormatted(false);

        return new Answer("Ich bin seit dem " + time + " aktiv!", AnswerType.FACTS, AnswerType.FACTS.isHidden());
    }
}
