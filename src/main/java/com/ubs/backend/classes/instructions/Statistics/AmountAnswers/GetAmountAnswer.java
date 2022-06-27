package com.ubs.backend.classes.instructions.Statistics.AmountAnswers;

import com.ubs.backend.classes.instructions.Statistics.Statistic;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Tim Irmler
 * @since 13.07.2021
 */
public class GetAmountAnswer {
    public GetAmountAnswer() {
    }

    public static Statistic getAmountAnswer() {
        ArrayList<Statistic> statistics = getAllAmountAnswers();
        Random r = new Random();
        return statistics.get(r.nextInt(statistics.size()));
    }

    private static ArrayList<Statistic> getAllAmountAnswers() {
        ArrayList<Statistic> statistics = new ArrayList<>();
        statistics.add(new AmountAnswerHour());
        statistics.add(new AmountAnswersDay());
        statistics.add(new AmountAnswersWeek());
        statistics.add(new AmountAnswersMonth());
        statistics.add(new AmountAnswersYear());

        return statistics;
    }
}
