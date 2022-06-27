package com.ubs.backend.classes;

import com.ubs.backend.classes.database.Answer;

/**
 * Class to save an answer, a tag, a rating and if the bot was certain
 *
 * @author Marc
 * @author Magnus
 * @since 17.07.2021
 */
public class Evaluation {
    /**
     * the answer
     *
     * @see Answer
     * @since 17.07.2021
     */
    private Answer answer;

    private String tag;

    private double rating;

    private boolean isCertain;

    /**
     * All-args constructor for the Evaluation
     *
     * @param answer the answer the bot found
     * @param tag    the tag corresponding to the answer
     * @param rating the rating of the tag
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public Evaluation(Answer answer, String tag, double rating) {
        super();
        this.answer = answer;
        this.tag = tag;
        this.rating = rating;
    }

    /**
     * no-args constructor for the Evaluation
     *
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public Evaluation() {

    }

    /**
     * @return the Answer the Bot found
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public Answer getAnswer() {
        return answer;
    }

    /**
     * @param answer the new Answer the Bot found.
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    /**
     * @return the Tag which the Bot matched with the answer
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the new Tag which the Bot matched with the answer
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the Rating the Bot calculated.
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public double getRating() {
        return rating;
    }

    /**
     * @param rating the new Rating the Bot calculated
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * @return if the Bot was certain.
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public boolean isCertain() {
        return isCertain;
    }

    /**
     * @param certain the new boolean if the bot was certain
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public void setCertain(boolean certain) {
        isCertain = certain;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "answer=" + answer +
                ", tag='" + tag + '\'' +
                ", rating=" + rating +
                ", isCertain=" + isCertain +
                '}';
    }
}
