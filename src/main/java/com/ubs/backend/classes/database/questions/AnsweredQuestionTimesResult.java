package com.ubs.backend.classes.database.questions;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.classes.database.statistik.AnsweredQuestionStatistik;

import javax.persistence.*;

/**
 * @author Tim Irmler
 * @since 04.08.2021
 */
@Entity
public class AnsweredQuestionTimesResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonField(type = JSONType.INTEGER)
    private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private AnsweredQuestionStatistik answeredQuestionStatistik;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private AnsweredQuestionResult answeredQuestionResult;

    @JsonField(type = JSONType.INTEGER)
    private int upvote = 0;

    @JsonField(type = JSONType.INTEGER)
    private int downvote = 0;

    public AnsweredQuestionTimesResult(AnsweredQuestionStatistik answeredQuestionStatistik, AnsweredQuestionResult answeredQuestionResult, int upvote, int downvote) {
        this.answeredQuestionStatistik = answeredQuestionStatistik;
        this.answeredQuestionResult = answeredQuestionResult;
        this.upvote = upvote;
        this.downvote = downvote;
    }

    public AnsweredQuestionTimesResult(AnsweredQuestionStatistik answeredQuestionStatistik, AnsweredQuestionResult answeredQuestionResult, long upvote, long downvote) {
        this.answeredQuestionStatistik = answeredQuestionStatistik;
        this.answeredQuestionResult = answeredQuestionResult;
        this.upvote = (int) upvote;
        this.downvote = (int) downvote;
    }

    public AnsweredQuestionTimesResult(AnsweredQuestionStatistik answeredQuestionStatistik, AnsweredQuestionResult answeredQuestionResult) {
        this.answeredQuestionStatistik = answeredQuestionStatistik;
        this.answeredQuestionResult = answeredQuestionResult;
    }

    public AnsweredQuestionTimesResult() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AnsweredQuestionStatistik getAnsweredQuestionStatistik() {
        return answeredQuestionStatistik;
    }

    public void setAnsweredQuestionStatistik(AnsweredQuestionStatistik answeredQuestionStatistik) {
        this.answeredQuestionStatistik = answeredQuestionStatistik;
    }

    public AnsweredQuestionResult getAnsweredQuestionResult() {
        return answeredQuestionResult;
    }

    public void setAnsweredQuestionResult(AnsweredQuestionResult answeredQuestionResult) {
        this.answeredQuestionResult = answeredQuestionResult;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    @Override
    public String toString() {
        return "AnsweredQuestionTimesResult{" +
                "id=" + id +
                ", answeredQuestionStatistik=" + answeredQuestionStatistik +
                ", answeredQuestionResult=" + answeredQuestionResult +
                ", upvote=" + upvote +
                ", downvote=" + downvote +
                '}';
    }
}
