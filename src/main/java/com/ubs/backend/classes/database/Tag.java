package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;

import javax.persistence.*;
import java.util.Objects;

/**
 * Dataclass to store a tag with its id and content
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Entity
@Table(name = "Tag")
@JsonSerializableObject(listName = "tags")
public class Tag {
    /**
     * The ID of this Tag in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tagID")
    @JsonField(type = JSONType.INTEGER, key = "id")
    private long tagID;

    /**
     * The Content of this Tag
     *
     * @since 17.07.2021
     */
    @Column(length = 64)
    @JsonField(type = JSONType.STRING)
    private String tag;

    /**
     * @return the ID of this Tag
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getTagID() {
        return tagID;
    }

    /**
     * @param tagID the new ID for this Tag
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setTagID(long tagID) {
        this.tagID = tagID;
    }

    /**
     * @return the Content of this Tag
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the new Content for this Tag
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Default Constructor
     *
     * @param tag the Content for the Tag
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Tag(String tag) {
        super();
        this.tag = tag;
    }

    /**
     * no-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Tag() {

    }

    /**
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    @Override
    public String toString() {
        return "Tag [tagID=" + tagID + ", tag=" + tag + "]";
    }

    /**
     * @param o
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag1 = (Tag) o;
        return tagID == tag1.tagID && Objects.equals(tag, tag1.tag);
    }

    /**
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagID, tag);
    }
}
