package com.ubs.backend.classes;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.Answer;

import java.util.ArrayList;

/**
 * Class for saving a temporary answer with all tags. used for building JSON objects
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
@JsonSerializableObject(listName = "tempAnswers")
public class TempAnswer {
    /**
     * The answer
     *
     * @see Answer
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Answer answer;

    /**
     * all the tags this answer has
     *
     * @see TempTag
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.LIST)
    private ArrayList<TempTag> tags = new ArrayList<>();

    /**
     * one single tag, used to count how many answers are using this tag
     *
     * @see TempTag
     * @since 17.07.2021
     */
    private long mySingleResultTagID;

    /**
     * the usefulness of this answer, calculated by the average usefulness of all its tags
     *
     * @since 21.07.2021
     */
    @JsonField(type = JSONType.FLOAT)
    private float usefulness;

    /**
     * used for tag detail site, if we want to display the amount of upvotes the single tag has on this single answer
     */
    @JsonField(type = JSONType.INTEGER)
    private int upvotes = 0;
    /**
     * used for tag detail site, if we want to display the amount of downvotes the single tag has on this single answer
     */
    @JsonField(type = JSONType.INTEGER)
    private int downvotes = 0;

    /**
     * @param answer
     * @param tags
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TempAnswer(Answer answer, ArrayList<TempTag> tags) {
        this.answer = answer;
        this.tags = tags;
    }

    /**
     * @param answer
     * @param mySingleResultTagID
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TempAnswer(Answer answer, long mySingleResultTagID) {
        this.answer = answer;
        this.mySingleResultTagID = mySingleResultTagID;
    }

    /**
     * @param answer
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TempAnswer(Answer answer) {
        this.answer = answer;
    }

    /**
     * @param tags
     * @param usefulness
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public TempAnswer(Answer answer, ArrayList<TempTag> tags, int usefulness) {
        this.answer = answer;
        this.tags = tags;
        this.usefulness = usefulness;
    }

    /**
     * @param answer
     * @param mySingleResultTagID
     * @param usefulness
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public TempAnswer(Answer answer, long mySingleResultTagID, int usefulness) {
        this.answer = answer;
        this.mySingleResultTagID = mySingleResultTagID;
        this.usefulness = usefulness;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Answer getAnswer() {
        return answer;
    }

    /**
     * @param answer
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public ArrayList<TempTag> getTags() {
        return tags;
    }

    /**
     * @param tags the new List of Tags
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setTags(ArrayList<TempTag> tags) {
        this.tags = tags;
    }

    /**
     * Method to add a Tag
     *
     * @param tag the new Tag
     * @return if the Tag was added
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public boolean addTag(TempTag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    /**
     * Method to remove a Tag
     *
     * @param tag the Tag being removed
     * @return if the Tag was removed
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public boolean removeTag(TempTag tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            return true;
        }
        return false;
    }

    /**
     * @return the ID of the single Tag
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getMySingleResultTagID() {
        return mySingleResultTagID;
    }

    /**
     * @param mySingleResultTagID the ID of the new single Tag
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setMySingleResultTagID(long mySingleResultTagID) {
        this.mySingleResultTagID = mySingleResultTagID;
    }

    public float getUsefulness() {
        return usefulness;
    }

    public void setUsefulness(float usefulness) {
        this.usefulness = usefulness;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void increaseUpvotes(int value) {
        this.upvotes += value;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public void increaseDownvotes(int value) {
        this.downvotes += value;
    }

    @Override
    public String toString() {
        return "TempAnswer{" +
                "answer=" + answer +
                ", tags=" + tags +
                ", mySingleResultTagID=" + mySingleResultTagID +
                ", usefulness=" + usefulness +
                ", upvotes=" + upvotes +
                ", downvotes=" + downvotes +
                '}';
    }
}
