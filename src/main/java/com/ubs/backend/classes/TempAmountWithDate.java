package com.ubs.backend.classes;

import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

/**
 * @author Tim Irmler
 * @since 25.08.2021
 */
public class TempAmountWithDate {
    public enum TempAmountWithDateType {
        ANSWER,
        ANSWERED_QUESTION,
        UNANSWERED_QUESTION,
    }

    private StatistikTimes times;
    private long amount;
    private TempAmountWithDateType type = TempAmountWithDateType.ANSWERED_QUESTION;

    public TempAmountWithDate(StatistikTimes times, long amount) {
        this.times = times;
        this.amount = amount;
    }

    public TempAmountWithDate(StatistikTimes times, long amount, TempAmountWithDateType type) {
        this.times = times;
        this.amount = amount;
        this.type = type;
    }

    public StatistikTimes getTimes() {
        return times;
    }

    public void setTimes(StatistikTimes times) {
        this.times = times;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public TempAmountWithDateType getType() {
        return type;
    }

    public void setType(TempAmountWithDateType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TempAmountWithDate{" +
                "times=" + times +
                ", amount=" + amount +
                ", type=" + type +
                '}';
    }
}
