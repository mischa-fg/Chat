package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;

import javax.persistence.*;

/**
 * Dataclass to store all Results with their answer, tag, upvotes, downvotes and usages
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Entity
@Table(name = "Results")
@JsonSerializableObject(listName = "results")
public class Result extends ResultParent {
    /**
     * The Answer for the Tag
     *
     * @see Answer
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Answer answer;

    /**
     * Default Constructor
     * If you create an answer in code, please use this one
     *
     * @param answer the Answer which will be matched with the Tag
     * @param tag    the Tag for this Result
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Result(Answer answer, Tag tag) {
        super(tag);
        this.answer = answer;
    }

    /**
     * All-args constructor
     *
     * @param answer    the Answer which will be matched with the Tag
     * @param tag       the Tag for this Result
     * @param upvotes   the amount of Upvotes
     * @param downvotes the amount of Downvotes
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Result(Answer answer, Tag tag, int upvotes, int downvotes) {
        super(tag, upvotes, downvotes);
        this.answer = answer;
    }

    /**
     * No-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Result() {
        super();
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultID=" + getId() +
                ", answer=" + getAnswer() +
                ", tag=" + getTag() +
                ", tagUpvotes=" + getUpvotes() +
                ", tagDownvotes=" + getDownvotes() +
                ", tagUsages=" + getUsages() +
                '}';
    }
}
