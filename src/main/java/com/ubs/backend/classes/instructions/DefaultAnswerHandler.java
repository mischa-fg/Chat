package com.ubs.backend.classes.instructions;

import com.ubs.backend.classes.database.Answer;

/**
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
public class DefaultAnswerHandler implements InstructionHandler {
    /**
     * @param answer the Answer which is being used
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    @Override
    public Answer handle(Answer answer) {
        return answer;
    }

    @Override
    public String toString() {
        return "DefaultAnswerHandler{}";
    }
}