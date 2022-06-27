package com.ubs.backend.classes.instructions;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.instructions.Statistics.AmountAnswers.GetAmountAnswer;
import com.ubs.backend.classes.instructions.Statistics.DBActiveSince;
import com.ubs.backend.classes.instructions.Statistics.Statistic;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Tim Irmler
 * @since 13.08.2021
 */
public class StatisticHandler implements InstructionHandler {
    @Override
    public Answer handle(Answer answer) {
        return generateAnswer();
    }

    private Answer generateAnswer() {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        ArrayList<Statistic> statistics = getAllStatisticTypes();
        Statistic statistic = getRandomStatisticType(statistics);

        Answer factAnswer = statistic.getFact(em);

        em.getTransaction().commit();
        em.close();

        return factAnswer;
    }

    private Statistic getRandomStatisticType(ArrayList<Statistic> statistics) {
        Random r = new Random();
        return statistics.get(r.nextInt(statistics.size()));
    }

    private ArrayList<Statistic> getAllStatisticTypes() {
        ArrayList<Statistic> statistics = new ArrayList<>();
        statistics.add(GetAmountAnswer.getAmountAnswer());
        statistics.add(new DBActiveSince());

        return statistics;
    }
}
