package com.ubs.backend.classes.database.questions;

import javax.persistence.*;

/**
 * Dataclass to store all Questions the Bot does not have an Answer for
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "UnansweredQuestions")
public class UnansweredQuestion {
    /**
     * The ID of this UnansweredQuestion in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unansweredQuestionID")
    private long unansweredQuestionID;

    /**
     * The Question which couldn't be answered
     *
     * @since 17.07.2021
     */
    private String question;

    /**
     * no-args constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public UnansweredQuestion() {
    }

    /**
     * Default Constructor
     *
     * @param question the Question which couldn't be answered
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public UnansweredQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "UnansweredQuestion{" +
                "unansweredQuestionID=" + unansweredQuestionID +
                ", question='" + question + '\'' +
                '}';
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getUnansweredQuestionID() {
        return unansweredQuestionID;
    }

    /**
     * @param unansweredQuestionID the new ID for this UnansweredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setUnansweredQuestionID(long unansweredQuestionID) {
        this.unansweredQuestionID = unansweredQuestionID;
    }

    /**
     * @return the Question which couldn't be answered
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the new Question for this UnansweredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setQuestion(String question) {
        this.question = question;
    }
}
