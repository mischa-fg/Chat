package com.ubs.backend.classes;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Result;
import com.ubs.backend.classes.database.ResultParent;

import java.util.Objects;

/**
 * Class to save a Word with the Result it matched and it's certainty
 *
 * @author Marc
 * @author Magnus
 * @since 17.07.2021
 */
public class BotResult {
    /**
     * the original result
     *
     * @see Result
     * @since 17.07.2021
     */
    private ResultParent result;

    private Answer answer;

    private String word;

    private double certainty;

    /**
     * Constructor with all args.
     *
     * @param result    the ResultParent with which the word matched
     * @param word      the Word
     * @param certainty the certainty of the match
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public BotResult(ResultParent result, Answer answer, String word, double certainty) {
        this.result = result;
        this.answer = answer;
        this.word = word;
        this.certainty = certainty;
    }

    /**
     * No-args constructor for BotResult
     *
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public BotResult() {
    }

    /**
     * @return the Word which was matched with the Result
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the new Word matched with the Result
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the Certainty of the BotResult
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public double getCertainty() {
        return certainty;
    }

    /**
     * @param certainty the new Certainty of the BotResult
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setCertainty(double certainty) {
        this.certainty = certainty;
    }

    /**
     * @param o
     * @return
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotResult botResult = (BotResult) o;
        return Double.compare(botResult.certainty, certainty) == 0 && Objects.equals(result, botResult.result) && Objects.equals(word, botResult.word);
    }

    public ResultParent getResult() {
        return result;
    }

    public void setResult(ResultParent result) {
        this.result = result;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    /**
     * @return
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    @Override
    public int hashCode() {
        return Objects.hash(result, word, certainty);
    }

    @Override
    public String toString() {
        return "BotResult{" +
                "result=" + result +
                ", word='" + word + '\'' +
                ", certainty=" + certainty +
                '}';
    }
}
