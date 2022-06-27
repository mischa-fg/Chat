package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.*;
import java.util.List;

/**
 * Dataclass to save an answer with the id
 *
 * @author Marc Andri Fuchs
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "Answers")
@JsonSerializableObject(listName = "answers")
public class Answer {
    /**
     * ID of the Answer in The Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answerID", length = 512)
    @JsonField(type = JSONType.INTEGER)
    private long answerID;

    /**
     * Answer which is being sent to the User
     *
     * @since 17.07.2021
     */
    @Column(length = 2048)
    @JsonField(type = JSONType.STRING)
    private String answer;

    /**
     * Title for this Answer.
     * Is displayed on the AdminTool
     *
     * @since 17.07.2021
     */
    @Column(length = 255)
    @JsonField(type = JSONType.STRING)
    private String title;

    /***
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.BOOLEAN)
    private boolean isHidden = true;

    /**
     * All UploadFiles for this Answer
     *
     * @see UploadFile
     * @since 17.07.2021
     */
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.LIST)
    private List<UploadFile> files;

    /**
     * the type of this answer
     *
     * @see AnswerType
     * @since 17.07.2021
     */
    @Enumerated(EnumType.ORDINAL)
    @JsonField(type = JSONType.ENUMERATED_ORDINAL)
    private AnswerType answerType;

    /**
     * Constructor with Title and Answer
     *
     * @param title      the Title of the Answer
     * @param answer     the Answerstring of the Answer
     * @param answerType the type of this answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Answer(String title, String answer, AnswerType answerType) {
        super();
        this.title = title;
        this.answer = answer;
        this.answerType = answerType;
        this.isHidden = answerType.isHidden();
    }

    /**
     * @param title
     * @param answer
     * @param answerType
     * @param isHidden
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Answer(String title, String answer, AnswerType answerType, boolean isHidden) {
        super();
        this.title = title;
        this.answer = answer;
        this.answerType = answerType;
        if (answerType.isForceHidden()) {
            this.isHidden = answerType.isHidden();
        } else {
            this.isHidden = isHidden;
        }
    }

    /**
     * Constructor with only the answer
     *
     * @param answer     the Answerstring of the Answer
     * @param answerType the type of this answer
     * @author Marc Andri Fuchs
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Answer(String answer, AnswerType answerType) {
        super();
        this.answer = answer;
        this.title = answer;
        this.answerType = answerType;
    }

    /**
     * @param answer
     * @param answerType
     * @param isHidden
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Answer(String answer, AnswerType answerType, boolean isHidden) {
        super();
        this.answer = answer;
        this.title = answer;
        this.isHidden = isHidden;
        this.answerType = answerType;
    }

    /**
     * no-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Answer() {

    }

    /**
     * @return the ID of the Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getAnswerID() {
        return answerID;
    }

    /**
     * @param answerID the new ID of the Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setAnswerID(long answerID) {
        this.answerID = answerID;
    }

    /**
     * @return this Answers Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer new Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return the Title of this Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the new Title for this Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return is Answer hidden
     * @author Tim Irmler
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * @param hidden is Answer Hidden
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    /**
     * @return a List of all UploadFiles in this Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public List<UploadFile> getFiles() {
        return files;
    }

    /**
     * @param files the new List of UploadFiles in this Answer
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    /**
     * @param file
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void addFile(UploadFile file) {
        files.add(file);
    }

    /**
     * @param file
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void removeFile(UploadFile file) {
        files.remove(file);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerID=" + answerID +
                ", answer='" + answer + '\'' +
                ", title='" + title + '\'' +
                ", isHidden=" + isHidden +
                ", files=" + files +
                ", answerType=" + answerType +
                '}';
    }

    /**
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public AnswerType getAnswerType() {
        return answerType;
    }

    /**
     * @param answerType
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }
}
