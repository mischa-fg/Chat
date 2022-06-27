package com.ubs.backend.classes.database.statistik;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@JsonSerializableObject(listName = "answeredQuestionStatistiks")
public class AnsweredQuestionStatistik {
    /**
     * The id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionStatistikID")
    @JsonField(type = JSONType.INTEGER)
    private long questionStatistikID;

    /**
     * the answered quesestion
     *
     * @see AnsweredQuestion
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "answeredQuestionID", foreignKey = @ForeignKey(name = "answeredQuestionID_FK"))
    @JsonField(type = JSONType.LIST)
    private AnsweredQuestion answeredQuestion;

    /**
     * the time when this question has been asked
     *
     * @see StatistikTimes
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "statistikTimesID", foreignKey = @ForeignKey(name = "answeredQuestionStatistikTimesID_FK"))
    @JsonField(type = JSONType.LIST)
    private StatistikTimes statistikTimes;

    /**
     * how often this question has been asked in this time period
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int askedAmount;

    /**
     * no args constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionStatistik() {
    }

    public AnsweredQuestionStatistik(AnsweredQuestionStatistik answeredQuestionStatistik, float thing) {

    }

    /**
     * @param answeredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionStatistik(AnsweredQuestion answeredQuestion) {
        this.answeredQuestion = answeredQuestion;
    }

    /**
     * @param answeredQuestion
     * @param statistikTimes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionStatistik(AnsweredQuestion answeredQuestion, StatistikTimes statistikTimes) {
        this.statistikTimes = statistikTimes;
        this.answeredQuestion = answeredQuestion;
    }

    /**
     * @param answeredQuestion
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionStatistik(AnsweredQuestion answeredQuestion, Date date) {
        this.statistikTimes = new StatistikTimes(date);
        this.answeredQuestion = answeredQuestion;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getQuestionStatistikID() {
        return questionStatistikID;
    }

    /**
     * @param questionStatistikID
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setQuestionStatistikID(long questionStatistikID) {
        this.questionStatistikID = questionStatistikID;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public int getAskedAmount() {
        return askedAmount;
    }

    /**
     * @param askedAmount
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAskedAmount(int askedAmount) {
        this.askedAmount = askedAmount;
    }

    /**
     * @param amount
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseAskedAmount(int amount) {
        this.askedAmount += amount;
    }

    /**
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseAskedAmountDefault() {
        this.increaseAskedAmount(1);
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimes getStatistikTimes() {
        return statistikTimes;
    }

    /**
     * @param statistikTimes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setStatistikTimes(StatistikTimes statistikTimes) {
        this.statistikTimes = statistikTimes;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestion getAnsweredQuestion() {
        return answeredQuestion;
    }

    /**
     * @param answeredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAnsweredQuestion(AnsweredQuestion answeredQuestion) {
        this.answeredQuestion = answeredQuestion;
    }

    @Override
    public String toString() {
        return "AnsweredQuestionStatistik{" +
                "questionStatistikID=" + questionStatistikID +
                ", answeredQuestion=" + answeredQuestion +
                ", statistikTimes=" + statistikTimes +
                ", askedAmount=" + askedAmount +
                '}';
    }
}
