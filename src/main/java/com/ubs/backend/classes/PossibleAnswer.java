package com.ubs.backend.classes;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.util.CalculateRating;

import java.util.ArrayList;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
public class PossibleAnswer {
    private final Answer answer;
    private ArrayList<WordLevenshteinDistance> foundWordLevenshteinDistances = new ArrayList<>();
    private int upvotes = 0;
    private int downvotes = 0;
    private float rating = 0f;

    public PossibleAnswer(Answer answer, WordLevenshteinDistance wordLevenshteinDistance) {
        this.answer = answer;
        this.foundWordLevenshteinDistances.add(wordLevenshteinDistance);
    }

    public PossibleAnswer(Answer answer, int upvotes, int downvotes, WordLevenshteinDistance wordLevenshteinDistance) {
        this.answer = answer;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.foundWordLevenshteinDistances.add(wordLevenshteinDistance);
    }

    public ArrayList<WordLevenshteinDistance> getFoundWordLevenshteinDistances() {
        return foundWordLevenshteinDistances;
    }

    public void setFoundWordLevenshteinDistances(ArrayList<WordLevenshteinDistance> foundWordLevenshteinDistances) {
        this.foundWordLevenshteinDistances = foundWordLevenshteinDistances;
    }

    public void addFoundWordLevenshteinDistance(ArrayList<WordLevenshteinDistance> foundWordLevenshteinDistance) {
        this.foundWordLevenshteinDistances.addAll(foundWordLevenshteinDistance);
    }

    public void addFoundWordLevenshteinDistance(WordLevenshteinDistance foundWordLevenshteinDistance) {
        this.foundWordLevenshteinDistances.add(foundWordLevenshteinDistance);
    }

    public Answer getAnswer() {
        return answer;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void increaseUpvotes(int amount) {
        this.upvotes += amount;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public void increaseDownvotes(int amount) {
        this.downvotes += amount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRating() {
        float totalDistance = 0;
        for (WordLevenshteinDistance wordLevenshteinDistance : foundWordLevenshteinDistances) {
            totalDistance += wordLevenshteinDistance.getLevenshteinDistance();
        }

        this.rating = CalculateRating.getRating(this.upvotes, this.downvotes, totalDistance);
    }

    @Override
    public String toString() {
        return "PossibleAnswer{" +
                "foundWordLevenshteinDistances=" + foundWordLevenshteinDistances +
                ", answer=" + answer +
                ", upvotes=" + upvotes +
                ", downvotes=" + downvotes +
                ", rating=" + rating +
                '}';
    }
}
