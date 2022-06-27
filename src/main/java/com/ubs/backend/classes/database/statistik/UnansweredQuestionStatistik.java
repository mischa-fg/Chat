package com.ubs.backend.classes.database.statistik;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.questions.UnansweredQuestion;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@JsonSerializableObject(listName = "unansweredQuestionStatistiks")
public class UnansweredQuestionStatistik {
    /**
     * the id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionStatistikID")
    @JsonField(type = JSONType.INTEGER)
    private long questionStatistikID;

    /**
     * the unanswered question
     *
     * @see UnansweredQuestion
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "unansweredQuestionID", foreignKey = @ForeignKey(name = "unansweredQuestionID_FK"))
    @JsonField(type = JSONType.LIST)
    private UnansweredQuestion unansweredQuestion;

    /**
     * the time when this question has been asked
     *
     * @see StatistikTimes
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "statistikTimesID", foreignKey = @ForeignKey(name = "unansweredQuestionStatistikTimesID_FK"))
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
    public UnansweredQuestionStatistik() {
    }

    /**
     * @param unansweredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public UnansweredQuestionStatistik(UnansweredQuestion unansweredQuestion) {
        this.unansweredQuestion = unansweredQuestion;
    }

    /**
     * @param unansweredQuestion
     * @param statistikTimes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public UnansweredQuestionStatistik(UnansweredQuestion unansweredQuestion, StatistikTimes statistikTimes) {
        this.statistikTimes = statistikTimes;
        this.unansweredQuestion = unansweredQuestion;
    }

    public UnansweredQuestionStatistik(UnansweredQuestion unansweredQuestion, StatistikTimes statistikTimes, Long askedAmount) {
        this.unansweredQuestion = unansweredQuestion;
        this.statistikTimes = statistikTimes;
        this.askedAmount = Math.toIntExact(askedAmount);
    }

    /**
     * @param unansweredQuestion
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public UnansweredQuestionStatistik(UnansweredQuestion unansweredQuestion, Date date) {
        this.statistikTimes = new StatistikTimes(date);
        this.unansweredQuestion = unansweredQuestion;
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
    public UnansweredQuestion getUnansweredQuestion() {
        return unansweredQuestion;
    }

    /**
     * @param unansweredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setUnansweredQuestion(UnansweredQuestion unansweredQuestion) {
        this.unansweredQuestion = unansweredQuestion;
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
}
