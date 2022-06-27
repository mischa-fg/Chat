package com.ubs.backend.classes;

import com.ubs.backend.classes.database.questions.UnansweredQuestion;

/**
 * @author Tim Irmler
 * @since 19.08.2021
 */
public class TempUnansweredQuestion {
    private UnansweredQuestion unansweredQuestion;
    private long views;

    public TempUnansweredQuestion(UnansweredQuestion unansweredQuestion, Long views) {
        this.unansweredQuestion = unansweredQuestion;
        this.views = views;
    }

    public TempUnansweredQuestion(UnansweredQuestion unansweredQuestion, long views) {
        this.unansweredQuestion = unansweredQuestion;
        this.views = views;
    }

    public UnansweredQuestion getUnansweredQuestion() {
        return unansweredQuestion;
    }

    public void setUnansweredQuestion(UnansweredQuestion unansweredQuestion) {
        this.unansweredQuestion = unansweredQuestion;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "TempUnansweredQuestion{" +
                "unansweredQuestion=" + unansweredQuestion +
                ", views=" + views +
                '}';
    }
}
