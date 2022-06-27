package com.ubs.backend.classes;

import com.ubs.backend.classes.database.questions.AnsweredQuestionTimesResult;

/**
 * @author Tim Irmler
 * @since 18.08.2021
 */
public class TempAnsweredQuestionTimesResult {
    private AnsweredQuestionTimesResult answeredQuestionTimesResult;
    private long views = 0;

    public TempAnsweredQuestionTimesResult(AnsweredQuestionTimesResult answeredQuestionTimesResult, long views) {
        this.answeredQuestionTimesResult = answeredQuestionTimesResult;
        this.views = views;
    }

    public TempAnsweredQuestionTimesResult(AnsweredQuestionTimesResult answeredQuestionTimesResult, Long views) {
        this.answeredQuestionTimesResult = answeredQuestionTimesResult;
        this.views = views;
    }

    public AnsweredQuestionTimesResult getAnsweredQuestionTimesResult() {
        return answeredQuestionTimesResult;
    }

    public void setAnsweredQuestionTimesResult(AnsweredQuestionTimesResult answeredQuestionTimesResult) {
        this.answeredQuestionTimesResult = answeredQuestionTimesResult;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "TempAnsweredQuestionTimesResult{" +
                "answeredQuestionTimesResult=" + answeredQuestionTimesResult +
                ", views=" + views +
                '}';
    }
}
