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
@Table(name = "Matches")
@JsonSerializableObject(listName = "matches")
public class Match {
    /**
     * The ID in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchID")
    @JsonField(type = JSONType.INTEGER, key = "id")
    private long matchID;

    /**
     * The Tag which is matched with the Word
     *
     * @since 17.07.2021
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Tag tag;

    /**
     * The Word which was matched with the Tag
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.STRING)
    private String word;

    /**
     * The amount of upvotes this Match has
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int upvote;

    /**
     * The amount of downvotes this Match has
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int downvote;

    /**
     * No-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Match() {

    }

    /**
     * All-args constructor
     *
     * @param tag      the Tag which was matched
     * @param word     the Word which was matched
     * @param upvote   the amount of Upvotes this Match has
     * @param downvote the amount of Downvotes this Match has
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Match(Tag tag, String word, int upvote, int downvote) {
        this.tag = tag;
        this.word = word;
        this.upvote = upvote;
        this.downvote = downvote;
    }

    /**
     * @param tag
     * @param word
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public Match(Tag tag, String word) {
        this.tag = tag;
        this.word = word;
        this.upvote = 0;
        this.downvote = 0;
    }

    /**
     * @return the ID of this match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getMatchID() {
        return matchID;
    }

    /**
     * @param id the new ID for the Match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setMatchID(int id) {
        this.matchID = id;
    }

    /**
     * @return the tag of this match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * @param tag the new Tag for this match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * @return the Word of this Match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the new Word for the this Match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the amount of Upvotes this Match received
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public int getUpvote() {
        return upvote;
    }

    /**
     * @param upvote the new amount of Upvotes for this Match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    /**
     * @return the amount of Downvotes this Match received
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public int getDownvote() {
        return downvote;
    }

    /**
     * @param downvote the new amount of Downvotes for this Match
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchID=" + matchID +
                ", tag=" + tag +
                ", word='" + word + '\'' +
                ", upvote=" + upvote +
                ", downvote=" + downvote +
                '}';
    }
}
