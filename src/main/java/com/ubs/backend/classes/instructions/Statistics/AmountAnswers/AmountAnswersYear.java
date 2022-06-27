package com.ubs.backend.classes.instructions.Statistics.AmountAnswers;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.dao.statistik.AnswerStatistikDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.instructions.Statistics.Statistic;

import javax.persistence.EntityManager;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
public class AmountAnswersYear implements Statistic {
    @Override
    public Answer getFact(EntityManager em) {
        AnswerStatistikDAO answerStatistikDAO = new AnswerStatistikDAO();
        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        StatistikTimes statistikTimes = statistikTimesDAO.selectNow(false, em);
        long amountAnswers = 0;

        if (statistikTimes != null) {
            amountAnswers = answerStatistikDAO.countAskedAmountSingleYear(statistikTimes, em);
        }

        String inhalt;

        if (amountAnswers > 0) {
            if (amountAnswers > 1) {
                inhalt = "In diesem Jahr wurden " + amountAnswers + " Fragen beantwortet!";
            } else {
                inhalt = "In diesem Jahr wurde " + amountAnswers + " Frage beantwortet!";
            }
        } else {
            inhalt = "In diesem Jahr wurden noch keine Fragen beantwortet!";
        }

        return new Answer(inhalt, AnswerType.FACTS, AnswerType.FACTS.isHidden());
    }
}
