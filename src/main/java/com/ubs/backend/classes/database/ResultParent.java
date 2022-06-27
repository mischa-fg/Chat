package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;

import javax.persistence.*;

/**
 * @author Tim Irmler
 * @since 19.07.2021
 */
@MappedSuperclass
public class ResultParent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonField(type = JSONType.INTEGER)
    private long id;

    /**
     * The Tag for the Answer
     *
     * @see Tag
     * @since 19.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.JSON_ANNOTATED)
    private Tag tag;

    /**
     * The amount of Upvotes this Result has received
     *
     * @since 19.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int upvotes = 0;

    /**
     * The amount of Downvotes this Result has received
     *
     * @since 19.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int downvotes = 0;

    /**
     * The Amount of usages this Result has
     *
     * @since 19.07.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private int usages = 0;

    public ResultParent() {
    }

    public ResultParent(Tag tag) {
        this.tag = tag;
        this.upvotes = 0;
        this.downvotes = 0;
        this.usages = 0;
    }

    public ResultParent(Tag tag, int upvotes, int downvotes) {
        this.tag = tag;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.usages = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int tagUpvotes) {
        this.upvotes = tagUpvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int tagDownvotes) {
        this.downvotes = tagDownvotes;
    }

    public int getUsages() {
        return usages;
    }

    public void setUsages(int tagUsages) {
        this.usages = tagUsages;
    }

    @Override
    public String toString() {
        return "ResultParent{" +
                "id=" + id +
                ", tag=" + tag +
                ", upvotes=" + upvotes +
                ", downvotes=" + downvotes +
                ", usages=" + usages +
                '}';
    }
}
