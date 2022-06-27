package com.ubs.backend.classes.database.questions;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.Result;
import com.ubs.backend.classes.database.ResultParent;
import com.ubs.backend.classes.database.TypeTag;

import javax.persistence.*;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "answeredQuestion_Result")
@JsonSerializableObject(listName = "answeredQuestion_Result")
public class AnsweredQuestionResult {
    /**
     * The ID of the answeredQuestionResult in the DB
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answeredQuestionResultID")
    @JsonField(type = JSONType.INTEGER)
    private long resultID;

    /**
     * The answered question with this result
     *
     * @see AnsweredQuestion
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "answeredQuestionID", foreignKey = @ForeignKey(name = "answeredQuestionIDForResult_FK"))
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private AnsweredQuestion answeredQuestion;

    /**
     * the result with this answered question
     *
     * @see Result
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Result result;

    /**
     * the typetag with this answered question
     *
     * @see TypeTag
     * @since 21.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private TypeTag typeTag;

    /**
     * default no args constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionResult() {

    }

    /**
     * @param resultID
     * @param answeredQuestion
     * @param result
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionResult(long resultID, AnsweredQuestion answeredQuestion, ResultParent result) {
        this.resultID = resultID;
        this.answeredQuestion = answeredQuestion;
        setResultParent(result);
    }

    /**
     * @param answeredQuestion
     * @param result
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestionResult(AnsweredQuestion answeredQuestion, ResultParent result) {
        this.answeredQuestion = answeredQuestion;
        setResultParent(result);
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getResultID() {
        return resultID;
    }

    /**
     * @param resultID
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setResultID(long resultID) {
        this.resultID = resultID;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public AnsweredQuestion getAnsweredQuestion() {
        return answeredQuestion;
    }

    /**
     * @param answeredQuestion
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAnsweredQuestion(AnsweredQuestion answeredQuestion) {
        this.answeredQuestion = answeredQuestion;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public TypeTag getTypeTag() {
        return typeTag;
    }

    /**
     * @param typeTag
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public void setTypeTag(TypeTag typeTag) {
        this.typeTag = typeTag;
    }

    /**
     * @param resultParent
     * @author Tim Irmler
     * @since 01.08.2021
     */
    public void setResultParent(ResultParent resultParent) {
        if (resultParent instanceof Result) {
            this.result = (Result) resultParent;
        } else if (resultParent instanceof TypeTag) {
            this.typeTag = (TypeTag) resultParent;
        }
    }

    @Override
    public String toString() {
        return "AnsweredQuestionResult{" +
                "resultID=" + resultID +
                ", answeredQuestion=" + answeredQuestion +
                ", result=" + result +
                ", typeTag=" + typeTag +
                '}';
    }
}
