package com.ubs.backend.classes;

import com.ubs.backend.classes.database.questions.AnsweredQuestion;

import java.util.List;

/**
 * @author Tim Irmler
 * @since 20.08.2021
 */
public class TempAnsweredQuestion {
    private AnsweredQuestion answeredQuestion;
    private List<TempAnswer> tempAnswers;
    private TempAnsweredQuestionTimesResult answeredQuestionTimesResult;

    public TempAnsweredQuestion(AnsweredQuestion answeredQuestion, List<TempAnswer> tempAnswers) {
        this.answeredQuestion = answeredQuestion;
        this.tempAnswers = tempAnswers;
    }

    public TempAnsweredQuestion(TempAnsweredQuestionTimesResult answeredQuestionTimesResult) {
        this.answeredQuestionTimesResult = answeredQuestionTimesResult;
    }

    public TempAnsweredQuestionTimesResult getAnsweredQuestionTimesResult() {
        return answeredQuestionTimesResult;
    }

    public void setAnsweredQuestionTimesResult(TempAnsweredQuestionTimesResult answeredQuestionTimesResult) {
        this.answeredQuestionTimesResult = answeredQuestionTimesResult;
    }

    public AnsweredQuestion getAnsweredQuestion() {
        return answeredQuestion;
    }

    public void setAnsweredQuestion(AnsweredQuestion answeredQuestion) {
        this.answeredQuestion = answeredQuestion;
    }

    public List<TempAnswer> getTempAnswers() {
        return tempAnswers;
    }

    public void setTempAnswers(List<TempAnswer> tempAnswers) {
        this.tempAnswers = tempAnswers;
    }

    @Override
    public String toString() {
        return "TempAnsweredQuestion{" +
                "answeredQuestion=" + answeredQuestion +
                ", tempAnswers=" + tempAnswers +
                ", answeredQuestionTimesResult=" + answeredQuestionTimesResult +
                '}';
    }
}
