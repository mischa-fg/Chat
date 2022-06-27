package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "TypeTag")
@JsonSerializableObject(listName = "typeTags")
public class TypeTag extends ResultParent {
    /**
     * the type of the tag, defined by the answers
     *
     * @see AnswerType
     * @since 17.07.2021
     */
    @Enumerated(EnumType.ORDINAL)
    private AnswerType answerType;

    /**
     * @param tag
     * @param answerType
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TypeTag(Tag tag, AnswerType answerType) {
        super(tag);
        this.answerType = answerType;
    }

    /**
     * All-args constructor
     *
     * @param tag       the Tag for this Result
     * @param upvotes   the amount of Upvotes
     * @param downvotes the amount of Downvotes
     * @author Tim Irmler
     * @since 19.07.2021
     */
    public TypeTag(Tag tag, AnswerType answerType, int upvotes, int downvotes) {
        super(tag, upvotes, downvotes);
        this.answerType = answerType;
    }

    /**
     * no args constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TypeTag() {
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnswerType getAnswerType() {
        return answerType;
    }

    /**
     * @param answerType
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    @Override
    public String toString() {
        return "TypeTag{" +
                "id=" + getId() +
                ", tag=" + getTag() +
                ", answerType=" + getAnswerType() +
                ", upvotes=" + getUpvotes() +
                ", downvotes=" + getDownvotes() +
                ", usages=" + getUsages() +
                '}';
    }
}
