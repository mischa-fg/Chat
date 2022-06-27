package com.ubs.backend.classes.database.questions;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.enums.DataTypeInfo;

import javax.persistence.*;

/**
 * Dataclass to save a default question
 *
 * @author sarah
 * @since 17.07.2021
 */
@Entity
@Table(name = "DefaultQuestions")
@JsonSerializableObject(listName = "defaultQuestions")
public class DefaultQuestion {
    /**
     * the id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defaultQuestionID")
    @JsonField(type = JSONType.INTEGER, key = "id")
    private long defaultQuestionID;

    /**
     * the default question
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.STRING, key = "question")
    @Column(length = 512)
    private String defaultQuestion;

    /**
     * no-args constructor
     *
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public DefaultQuestion() {

    }

    /**
     * default constructor
     *
     * @param defaultQuestion the default question string
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public DefaultQuestion(String defaultQuestion) {
        this.defaultQuestion = defaultQuestion;
    }

    /**
     * @return the ID of this default question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public long getDefaultQuestionID() {
        return defaultQuestionID;
    }

    /**
     * @param defaultQuestionId the new ID of this default question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public void setDefaultQuestionID(long defaultQuestionId) {
        this.defaultQuestionID = defaultQuestionId;
    }

    /**
     * @return the default question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public String getDefaultQuestion() {
        return defaultQuestion;
    }

    /**
     * @param defaultQuestion new Question for this default question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    public void setDefaultQuestion(String defaultQuestion) {
        this.defaultQuestion = defaultQuestion;
    }

    @Override
    public String toString() {
        return "DefaultQuestions{" +
                "defaultQuestionId=" + defaultQuestionID +
                ", defaultQuestion='" + defaultQuestion + '\'' +
                '}';
    }
}
