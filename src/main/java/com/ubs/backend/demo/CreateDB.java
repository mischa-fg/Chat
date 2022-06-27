package com.ubs.backend.demo;

import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.*;
import com.ubs.backend.classes.database.dao.questions.DefaultQuestionDAO;
import com.ubs.backend.classes.database.questions.DefaultQuestion;
import com.ubs.backend.classes.database.statistik.CreationOfDB;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Class to create a Database according to the Dataclasses
 *
 * @author Marc Andri Fuchs
 * @author Magnus Gerd Georg Goetz
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class CreateDB {
    /**
     * @param args
     * @throws Exception
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    public static void main(String[] args) throws Exception {
        DBData data = new DBData();

        ResultDAO resultDAO = new ResultDAO();
        BlacklistEntryDAO blacklistDAO = new BlacklistEntryDAO();
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        AnswerDAO answerDAO = new AnswerDAO();

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        em.persist(new CreationOfDB(em));

        List<Result> results = data.getResults();
        for (Result r : results) {
            resultDAO.insert(r, em);
        }

        List<BlacklistEntry> blacklist = data.getBlackList();
        for (BlacklistEntry b : blacklist) {
            blacklistDAO.insert(b, em);
        }

        List<UserLogin> users = data.getUserLogins();
        for (UserLogin u : users) {
            userLoginDAO.insert(u, em);
        }

        List<DefaultQuestion> defaultQuestions = data.getDefaultQuestions();
        for (DefaultQuestion dQ : defaultQuestions) {
            defaultQuestionDAO.insert(dQ, em);
        }

        List<TypeTag> typeTags = data.getTypeTags();
        for (TypeTag typeTag : typeTags) {
            typeTagDAO.insert(typeTag, em);
        }

        List<Answer> answers = data.getTypeTagAnswers();
        for (Answer answer : answers) {
            answerDAO.insert(answer, em);
        }

        em.getTransaction().commit();
        em.close();
    }
}
