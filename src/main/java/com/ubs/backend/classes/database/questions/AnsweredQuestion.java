package com.ubs.backend.classes.database.questions;

import javax.persistence.*;

/**
 * Dataclass to save an question with the today Usages
 *
 * @author Sarah Ambi
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "AnsweredQuestions")
public class AnsweredQuestion {
    /**
     * the id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answeredQuestionID")
    private long answeredQuestionID;

    /**
     * The question itself
     *
     * @since 17.07.2021
     */
    private String question;

    /**
     * if the answer of this question is hidden, this question must be hidden too
     *
     * @since 17.07.2021
     */
    boolean isHidden = false;

    /**
     * no-args constructor
     *
     * @author Sarah
     * @since 17.07.2021
     */
    public AnsweredQuestion() {
    }

    /**
     * default constructor
     *
     * @param question
     * @param isHidden
     * @author Sarah
     * @since 17.07.2021
     */
    public AnsweredQuestion(String question, boolean isHidden) {
        this.question = question;
        this.isHidden = isHidden;
    }

    @Override
    public String toString() {
        return "AnsweredQuestion{" +
                "answeredQuestionID=" + answeredQuestionID +
                ", question='" + question + '\'' +
                ", isHidden=" + isHidden +
                '}';
    }

    /**
     * @return the ID of the answered question
     * @author Sarah
     * @since 17.07.2021
     */
    public long getAnsweredQuestionID() {
        return answeredQuestionID;
    }

    /**
     * @param answeredQuestionID the new ID of this answered question
     * @author Sarah
     * @since 17.07.2021
     */
    public void setAnsweredQuestionID(long answeredQuestionID) {
        this.answeredQuestionID = answeredQuestionID;
    }

    /**
     * @return the answered question
     * @author Sarah
     * @since 17.07.2021
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question new question for this answered question
     * @author Sarah
     * @since 17.07.2021
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return is answered question hidden
     * @author Sarah
     * @since 17.07.2021
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * @param hidden is answered question hidden
     * @author Sarah
     * @since 17.07.2021
     */
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}