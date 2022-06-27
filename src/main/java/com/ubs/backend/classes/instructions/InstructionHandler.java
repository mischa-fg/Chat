package com.ubs.backend.classes.instructions;

import com.ubs.backend.classes.database.Answer;

/**
 * Interface for all Handlers of AnswerTypes
 *
 * @author Marc
 * @since 17.07.2021
 */
public interface InstructionHandler {
    /**
     * Handles the Response for the answer
     *
     * @param answer the Answer which is being used
     * @return the Response for the Server
     * @author Marc
     * @since 17.07.2021
     */
    Answer handle(Answer answer);
}