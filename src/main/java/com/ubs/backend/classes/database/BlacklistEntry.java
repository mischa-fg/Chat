package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;

import javax.persistence.*;

/**
 * Dataclass to store all Words which are going to be ignored by the word matching algorithm
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Entity
@Table(name = "Blacklist")
@JsonSerializableObject(listName = "blacklist")
public class BlacklistEntry {
    /**
     * The ID of the BlacklistEntry in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entryID", nullable = false)
    @JsonField(type = JSONType.INTEGER, key = "id")
    private long entryID;

    /**
     * The Word which is being blacklisted
     *
     * @since 17.07.2021
     */
    @Column(length = 64)
    @JsonField(type = JSONType.STRING)
    private String word;

    /**
     * The amount of times this Word was used
     */
    @JsonField(type = JSONType.INTEGER)
    private int usages;

    public long getEntryID() {
        return entryID;
    }

    /**
     * All-args constructor
     *
     * @param word the Word which is being blacklisted
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public BlacklistEntry(String word) {
        this.word = word;
        this.usages = 0;
    }

    /**
     * No-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public BlacklistEntry() {
    }

    /**
     * @return the ID of the BlacklistEntry
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getBlackListID() {
        return entryID;
    }

    /**
     * @param blackListID the new ID of the BlacklistEntry
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setBlackListID(long blackListID) {
        this.entryID = blackListID;
    }

    /**
     * @return the Word which is being blacklisted
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the new Word which will be blacklisted
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the amount of usages of this word
     */
    public int getUsages() {
        return usages;
    }

    /**
     * @param usages the new amount of usages
     */
    public void setUsages(int usages) {
        this.usages = usages;
    }
}
