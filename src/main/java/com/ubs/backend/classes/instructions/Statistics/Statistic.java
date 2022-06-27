package com.ubs.backend.classes.instructions.Statistics;

import com.ubs.backend.classes.database.Answer;

import javax.persistence.EntityManager;

public interface Statistic {
    /**
     * @param em the EntityManager to use
     * @return
     */
    Answer getFact(EntityManager em);
}
