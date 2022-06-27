package com.ubs.backend.classes.enums;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.instructions.*;

/**
 * Enum to identify different types of Answers
 *
 * @author Marc
 * @author Tim Irmler
 * @since 17.07.2021
 */
public enum AnswerType {
    /**
     * Normal Answer with one Response String
     *
     * @since 17.07.2021
     */
    DEFAULT("Normal", new DefaultAnswerHandler(), true, false, false, false),

    /**
     * Answer which contains a random joke
     *
     * @since 17.07.2021
     */
    JOKE("Witz", new JokeHandler(), true, true, true, true),

    /**
     * Enum with facts about the bot
     *
     * @since 08.08.2021
     */
    FACTS("Facts", new FactsHandler(), true, true, true, true),

    /**
     * Contains procedurally generated answers with statistics about the bot
     *
     * @since 12.08.2021
     */
    STATISTICS("Statistiken", new StatisticHandler(), false, true, true, true),

    /**
     * used if we return an error! can't be used as an actual answertype
     *
     * @since 13.08.2021
     */
    ERROR("Fehler", null, false, false, true, true);

    /**
     * The Object which is being used to handle the Answer
     *
     * @since 17.07.2021
     */
    private final InstructionHandler handler;

    /**
     * The name of the answer type, used to display on the website
     *
     * @since 17.07.2021
     */
    private final String name;

    /**
     * boolean to determine if a user can create an answer with this type
     *
     * @since 17.07.2021
     */
    private final boolean canBeUserMade;

    /**
     * boolean to determine if all answers share the same tags or if they don't
     *
     * @since 17.07.2021
     */
    private final boolean groupedTags;

    /**
     * defines if all answers are hidden in the statistics or not.
     * is only the default value. if not forced, user can define something else
     *
     * @since 21.07.2021
     */
    private final boolean hidden;

    /**
     * need all the answers of this type to be hidden/not hidden?
     *
     * @since 08.08.2021
     */
    private final boolean forceHidden;

    /**
     * Default constructor
     *
     * @param name
     * @param canBeUserMade
     * @param groupedTags
     * @author Marc
     * @since 17.07.2021
     */
    AnswerType(String name, InstructionHandler handler, boolean canBeUserMade, boolean groupedTags, boolean hidden, boolean forceHidden) {
        this.handler = handler;
        this.name = name;
        this.canBeUserMade = canBeUserMade;
        this.groupedTags = groupedTags;
        this.hidden = hidden;
        this.forceHidden = forceHidden;
    }

    /**
     * cached values
     *
     * @since 17.07.2021
     */
    private static final AnswerType[] values = values();

    /**
     * Handles the Answer String for the Type of Answer
     *
     * @param answer the answer which is going to be used
     * @return the Response which is being sent to the client
     * @author Marc
     * @since 17.07.2021
     */
    public Answer handle(Answer answer) {
        return handler.handle(answer);
    }

    /**
     * get the handler object
     *
     * @return the handler object
     * @author Marc
     * @since 17.07.2021
     */
    public InstructionHandler getHandler() {
        return handler;
    }

    /**
     * get the name of the answer type
     *
     * @return the name of the answer type
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public String getName() {
        return name;
    }

    /**
     * get the boolean to determine if this answer type can be used by users to create an answer with it
     *
     * @return the boolean to determine if this answer type can be used to create an answer with it
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public boolean canBeUserMade() {
        return canBeUserMade;
    }

    /**
     * get the boolean to determine if this answer type has grouped tags or not
     *
     * @return the boolean to determine if this answer type has grouped tags or not
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public boolean isGroupedTags() {
        return groupedTags;
    }

    /**
     * @return the cached values, containing all enum elements
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public static AnswerType[] getValues() {
        return values;
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * method to get an answerType by its name
     *
     * @param name the name we provide to search for an answer type
     * @return the found answer type, if none is found it returns null
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public static AnswerType getAnswerTypeByName(String name) {
        for (AnswerType answerType : AnswerType.getValues()) {
            if (answerType.name.equalsIgnoreCase(name)) {
                return answerType;
            }
        }
        return null;
    }

    public boolean isForceHidden() {
        return forceHidden;
    }

    @Override
    public String toString() {
        return "AnswerType{" +
                "handler=" + handler +
                ", name='" + name + '\'' +
                ", canBeUserMade=" + canBeUserMade +
                ", groupedTags=" + groupedTags +
                ", hidden=" + hidden +
                ", forceHidden=" + forceHidden +
                '}';
    }
}
