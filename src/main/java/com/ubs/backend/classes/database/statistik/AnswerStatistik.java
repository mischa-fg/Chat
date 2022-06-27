package com.ubs.backend.classes.database.statistik;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

import javax.persistence.*;

/**
 * @author Tim Irmler
 * @since 29.08.2021
 */
@Entity
public class AnswerStatistik {
    /**
     * The id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonField(type = JSONType.INTEGER)
    private long answerStatistikID;

    /**
     * the answer
     *
     * @see Answer
     * @since 29.08.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.LIST)
    private Answer answer;

    /**
     * the time when this answer has been sent
     *
     * @see StatistikTimes
     * @since 29.08.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.LIST)
    private StatistikTimes statistikTimes;

    /**
     * how often this question has been asked in this time period
     *
     * @since 29.08.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int askedAmount = 0;

    public AnswerStatistik(Answer answer, StatistikTimes statistikTimes) {
        this.answer = answer;
        this.statistikTimes = statistikTimes;
    }

    public AnswerStatistik(Answer answer, StatistikTimes statistikTimes, int askedAmount) {
        this.answer = answer;
        this.statistikTimes = statistikTimes;
        this.askedAmount = askedAmount;
    }

    public AnswerStatistik() {

    }

    public long getAnswerStatistikID() {
        return answerStatistikID;
    }

    public void setAnswerStatistikID(long answerStatistikID) {
        this.answerStatistikID = answerStatistikID;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public StatistikTimes getStatistikTimes() {
        return statistikTimes;
    }

    public void setStatistikTimes(StatistikTimes statistikTimes) {
        this.statistikTimes = statistikTimes;
    }

    public int getAskedAmount() {
        return askedAmount;
    }

    public void setAskedAmount(int askedAmount) {
        this.askedAmount = askedAmount;
    }
}
