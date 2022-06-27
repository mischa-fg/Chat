package com.ubs.backend.services;

import com.ubs.backend.classes.database.Answer;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.AnswerDAO;
import com.ubs.backend.classes.database.dao.MatchDAO;
import com.ubs.backend.classes.database.dao.ResultDAO;
import com.ubs.backend.classes.database.dao.TypeTagDAO;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.enums.DataTypeInfo;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrepareString.prepareString;
import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * Service to up/downvote an answer/tag combo for the Chatbot
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Path("feedback")
public class VoteService {
    /**
     * The main function to up/downvote
     *
     * @param answerIdString   the answer from the bot
     * @param voteParamBoolean boolean if it is an upvote
     * @param isRevert
     * @param tagList          the list of all Tags
     * @param matchList
     * @param question
     * @return Response status 200 without content
     * @author Marc Andri Fuchs
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/vote")
    public Response vote(@QueryParam("answerID") String answerIdString, @QueryParam("result") String voteParamBoolean, @QueryParam("revert") String isRevert, @QueryParam("tags") List<String> tagList, @QueryParam("matches") List<String> matchList, @QueryParam("question") String question) {
        question = prepareString(question, DataTypeInfo.USER_QUESTION_INPUT.getMaxLength(), true, false, false);
        long answerID;
        boolean isUpvote;
        boolean revert;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        try { // Cast Parameters to variables with correct type
            answerID = Long.parseLong(answerIdString);
            isUpvote = Boolean.parseBoolean(voteParamBoolean);
            revert = Boolean.parseBoolean(isRevert);
        } catch (Exception e) {
            em.getTransaction().commit();
            em.close();

            return Response.status(400).build();
        }

        AnswerDAO answerDAO = new AnswerDAO();
        Answer answer = answerDAO.select(answerID, em);
        if (answer == null) {
            em.getTransaction().commit();
            em.close();

            return Response.status(400).entity("\"error\": \"Diese Antwort existiert nicht\"").build();
        }

        AnswerType answerType = answer.getAnswerType();

        if (answerID < 1) {
            if (!answerType.isGroupedTags()) {
                em.getTransaction().commit();
                em.close();

                return Response.status(400).entity("\"error\": \"AnswerID is lower then one but type is not of grouped tags\"").build();
            }
        } else {
            answer = answerDAO.select(answerID, em);

            if (answer == null) {
                em.getTransaction().commit();
                em.close();

                return Response.status(400).entity("\"error\": \"Answer not found!\"").build();
            }
        }

        ArrayList<Long> tags = new ArrayList<>();
        ArrayList<Long> matches = new ArrayList<>();

        if (tagList.size() > 0 && !tagList.get(0).equals("")) {
            for (String s : tagList) {
                try {
                    tags.add(Long.parseLong(s));
                } catch (NumberFormatException e) {
                    printDebug("number format exception", "in parsing tagList in VoteService");
                }
            }
        }

        if (matchList.size() > 0 && !matchList.get(0).equals("")) {
            for (String s : matchList) {
                try {
                    matches.add(Long.parseLong(s));
                } catch (NumberFormatException e) {
                    printDebug("number format exception", "in parsing matchList in VoteService");
                }
            }
        }

        ResultDAO resultDAO = new ResultDAO();
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        MatchDAO matchDAO = new MatchDAO();

        printDebug("tags to vote", tags);
        for (Long id : tags) {
            printDebug("current tag", id);
            if (answerType.isGroupedTags()) {
                typeTagDAO.vote(answerType, id, isUpvote, revert, question, em);
            } else {
                resultDAO.vote(answerID, id, isUpvote, revert, question, em);
            }
        }

        printDebug("matches to vote", matches);
        for (Long id : matches) {
            printDebug("current match", id);
            matchDAO.vote(id, answerID, isUpvote, revert, question, em);
        }

        em.getTransaction().commit();
        em.close();

        return Response.status(200).build();
    }
}
