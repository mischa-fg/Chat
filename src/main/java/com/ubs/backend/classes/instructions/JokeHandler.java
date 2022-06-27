package com.ubs.backend.classes.instructions;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.dao.AnswerDAO;
import com.ubs.backend.classes.enums.AnswerType;

import java.util.List;

/**
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
public class JokeHandler implements InstructionHandler {
    /**
     * @param answer the Answer which is being used
     * @return the parameter
     * @author Marc Andri Fuchs
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Override
    public Answer handle(Answer answer) {
        AnswerDAO dao = new AnswerDAO();
        List<Answer> jokes = dao.selectRandomByType(AnswerType.JOKE, 1);

        if (jokes != null) {
            return jokes.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "JokeHandler{}";
    }
}