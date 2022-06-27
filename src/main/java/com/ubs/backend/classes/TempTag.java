package com.ubs.backend.classes;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Tag;
import com.ubs.backend.classes.database.dao.AnswerDAO;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * Tag used for temporary saving a tag and building JSON objects.
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
@JsonSerializableObject(listName = "tempTags")
public class TempTag {
    /**
     * The tag
     *
     * @see Tag
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Tag tag;

    /**
     * Amount of upvotes this tag in this context has received
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int upvotes = 0;

    /**
     * Amount of downvotes this tag in this context has received
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int downvotes = 0;

    /**
     * how often the tag has been used
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER, key = "usage")
    private int tagUsages = 0;

    /**
     * amount of answers using this tag
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int amountAnswers = 0;

    /**
     * the answer this specific tag belongs to in a result set
     *
     * @see TempAnswer
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private long singleAnswerID;

    /**
     * All Answers that use this tag
     *
     * @see TempAnswer
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.LIST)
    private ArrayList<TempAnswer> allAnswers = new ArrayList<>();

    /**
     * default constructor
     */
    public TempTag() {

    }

    /**
     * used when we need to figure out to what specific result and answer this tag belongs to
     *
     * @param tag            the default tag we want to save
     * @param singleAnswerID the id of the answer in the db to which this tag belongs to
     * @param upvotes        the amount of upvotes this tag has in the db in this result set
     * @param downvotes      the amount of downvotes this tag has in the db in this result set
     * @param tagUsages      how often this tag has been used with this answer
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TempTag(Tag tag, long singleAnswerID, int upvotes, int downvotes, int tagUsages) {
        this.tag = tag;
        this.singleAnswerID = singleAnswerID;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.tagUsages = tagUsages;
    }

    /**
     * save tag but only with its id and its name.
     *
     * @param tag the tag we want to save
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TempTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * @param tag the {@link TempTag} from where we take its values and increase ours by
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseAllValues(TempTag tag) {
        printDebug("new Method", "TempTag.increaseAllValues");
        printDebug("new method parameters", "tag = " + tag);
        this.increaseTagUsages(tag.getTagUsages());
        this.increaseAmountAnswers(tag.getAmountAnswers());
        this.increaseUpvotes(tag.getUpvotes());
        this.increaseDownvotes(tag.getDownvotes());
    }

    /**
     * @return the tag of this instance
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * @return the ID of the Answer
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getSingleAnswerID() {
        return singleAnswerID;
    }

    /**
     * @param singleAnswerID the ID of the new Answer
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setSingleAnswerID(long singleAnswerID) {
        this.singleAnswerID = singleAnswerID;
    }

    /**
     * @return a List of all Answers
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public ArrayList<TempAnswer> getAllAnswers() {
        return allAnswers;
    }

    /**
     * @param allAnswers the new List of Answers
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAllAnswers(ArrayList<TempAnswer> allAnswers) {
        this.allAnswers = allAnswers;
    }

    /**
     * @param answers all
     * @author Tim Irmler
     * @since 28.07.2021
     */
    public void setAllAnswers(List<Answer> answers) {
        this.allAnswers = getTempAnswersFromAnswers(answers);
    }

    /**
     * adds the given {@link TempAnswer}s to this instance
     *
     * @param answers a list of all the {@link TempAnswer}s
     * @author Tim Irmler
     * @since 28.07.2021
     */
    public void addAnswers(ArrayList<TempAnswer> answers) {
        this.allAnswers.addAll(answers);
    }

    /**
     * calculates the average usefulness of every given answer and adds it to this instance
     *
     * @param tempAnswers every {@link TempAnswer} that we want to add and calculate the usefulness from
     * @param answerDAO   the answerDAO to access the DB
     * @param em          the entity manager
     * @author Tim Irmler
     * @since 28.07.2021
     */
    public void addAnswersWithUsefulness(ArrayList<TempAnswer> tempAnswers, AnswerDAO answerDAO, EntityManager em) {
        for (TempAnswer tempAnswer : tempAnswers) {
            tempAnswer.setUsefulness(getUsefulness(tempAnswer, answerDAO, em));
        }
        addAnswers(tempAnswers);
    }

    /**
     * @param answers the {@link Answer}s of which {@link TempAnswer}s should be created from
     * @return a list of the newly created {@link TempAnswer}s
     * @author Tim Irmler
     * @since 28.07.2021
     */
    public ArrayList<TempAnswer> getTempAnswersFromAnswers(List<Answer> answers) {
        ArrayList<TempAnswer> tempAnswers = new ArrayList<>();
        for (Answer answer : answers) {
            tempAnswers.add(new TempAnswer(answer));
        }
        return tempAnswers;
    }

    /**
     * method to calculate the average usefulness of an answer by accessing the DB
     *
     * @param tempAnswer the tempAnswer to get the usefulness from
     * @param answerDAO  the DB access object to get the information from the DB
     * @param em         the entity manager
     * @return the average usefulness of the answer as a floating point number
     * @author Tim Irmler
     * @see AnswerDAO#getAverageUsefulness(long, EntityManager)
     * @since 28.07.2021
     */
    public float getUsefulness(TempAnswer tempAnswer, AnswerDAO answerDAO, EntityManager em) {
        return answerDAO.getAverageUsefulness(tempAnswer.getAnswer().getAnswerID(), em);
    }

    /**
     * @return the amount of Upvotes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * @param upvotes the new Amount of upvotes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    /**
     * @param value the value by which the upvotes should be increased
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseUpvotes(int value) {
        this.upvotes += value;
    }

    /**
     * @return the amount of Downvotes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public int getDownvotes() {
        return downvotes;
    }

    /**
     * @param downvotes the new amount of Downvotes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    /**
     * @param value the value by which the amount of Downvotes should be increased
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseDownvotes(int value) {
        this.downvotes += value;
    }

    /**
     * @return the amount of TagUsages
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public int getTagUsages() {
        return tagUsages;
    }

    /**
     * @param tagUsages how often this tag has been used
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setTagUsages(int tagUsages) {
        this.tagUsages = tagUsages;
    }

    /**
     * @param value the value the Tag Usages should be increased by
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseTagUsages(int value) {
        this.tagUsages += value;
    }

    /**
     * @return the amount of Answers this TempTag has
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public int getAmountAnswers() {
        return amountAnswers;
    }

    /**
     * @param amountAnswers the amount of answers
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAmountAnswers(int amountAnswers) {
        this.amountAnswers = amountAnswers;
    }

    /**
     * @param value the value the AmountAnswers should be increased by
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void increaseAmountAnswers(int value) {
        this.amountAnswers += value;
    }

    @Override
    public String toString() {
        return "TempTag{" +
                "tag=" + tag +
                ", upvotes=" + upvotes +
                ", downvotes=" + downvotes +
                ", tagUsages=" + tagUsages +
                ", amountAnswers=" + amountAnswers +
                ", singleAnswerID=" + singleAnswerID +
                ", allAnswers=" + allAnswers +
                '}';
    }
}
