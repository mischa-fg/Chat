package com.ubs.backend.services;

import com.ubs.backend.annotations.json.JSONParser;
import com.ubs.backend.annotations.json.ParserResponseType;
import com.ubs.backend.classes.*;
import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.*;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionTimesResultDAO;
import com.ubs.backend.classes.database.dao.questions.DefaultQuestionDAO;
import com.ubs.backend.classes.database.dao.statistik.UnansweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.AnsweredQuestionTimesResult;
import com.ubs.backend.classes.database.questions.DefaultQuestion;
import com.ubs.backend.classes.database.questions.UnansweredQuestion;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.enums.DataTypeInfo;
import com.ubs.backend.classes.enums.TimeSearchType;
import com.ubs.backend.util.CalculateRating;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.ubs.backend.util.GetTimeRange.getTimeRangeByString;
import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * All Services which return some JSON Object or List of Database Entries
 *
 * @author Tim Irmler
 * @author Marc
 * @author Magnus
 * @author Sarah
 * @since 17.07.2021
 */
@Path("get")
public class Get {
    /**
     * Get a single answer with all the tags it's using
     *
     * @param answerID the id of the answer that we want
     * @return a TempTag object
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/singleAnswer")
    public Response getSingleAnswer(@QueryParam("answerID") long answerID) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();
        ArrayList<TempAnswer> tempAnswers = new ArrayList<>();
        TempAnswer tempAnswer = addTagsToTempAnswer(answerDAO.getSingleTempAnswer(answerID, em).getAnswer(), -1, em);
        tempAnswers.add(tempAnswer);

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity(generateJSONResponseAllAnswers(tempAnswers)).build();
    }

    /**
     * returns a json object containing a single question
     *
     * @param defaultQuestionId the id of the question to be returned
     * @return json object with single question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("singleDefaultQuestion")
    public Response getSingleDefaultQuestion(@QueryParam("defaultQuestionId") long defaultQuestionId) {
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();

        DefaultQuestion defaultQuestion = defaultQuestionDAO.select(defaultQuestionId);
        ArrayList<DefaultQuestion> defaultQuestionList = new ArrayList<>();
        defaultQuestionList.add(defaultQuestion);

        return Response.ok().entity(generateJSONResponseAllDefaultQuestion(defaultQuestionList)).build();
    }

    /**
     * Service to get a Single Tag from the Database
     *
     * @param tagID the ID of the Tag from Path
     * @return the Tag as a JSON Object
     * @author Tim Irmler
     * @author Magnus
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/singleTag")
    public Response getSingleTag(@QueryParam("tagID") long tagID) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        TempTag tempTag = getSingleTagWithExtraInformations(tagID, null, true, em);

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity(generateJSONResponseSingleTag(tempTag)).build();
    }

    /**
     * @param tagLimitString defines the maximum number of tags that will be returned per answer.
     * @return
     * @author Magnus
     * @author Tim Irmler
     * @since 20.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allAnswersWhereTagId")
    public Response getAllAnswersWhereTagId(@QueryParam("tagId") String tagLimitString) {
        long tagId = Long.parseLong(tagLimitString);

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();

        List<Result> resultsFromDB = answerDAO.selectByTag(tagId, em);
        List<Answer> answersFromDB = new ArrayList<>();
        for (Result r : resultsFromDB) {
            boolean found = false;
            for (Answer a : answersFromDB) {
                if (r.getAnswer().equals(a)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                answersFromDB.add(r.getAnswer());
        }
        ArrayList<TempAnswer> answers = new ArrayList<>();

        // Go through all answers in the DB
        for (Answer currentDBAnswer : answersFromDB) {
            // Create a new tempAnswer
            TempAnswer tempAnswer = addTagsToTempAnswer(currentDBAnswer, answersFromDB.size(), em);

            answers.add(tempAnswer);
        }

        em.getTransaction().commit();
        em.close();

        // Build the JSON object
        return Response.ok().entity(generateJSONResponseAllAnswers(answers)).build();
    }

    /**
     * Restful service that returns all answers with a max set amount of tags.
     *
     * @param tagLimitString defines the maximum number of tags that will be returned per answer.
     * @return returns a json object containing all answers with corresponding tags.
     * @author Tim Irmler
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allAnswers")
    public Response getAllAnswers(@QueryParam("tagLimit") String tagLimitString) {
        int tagLimit = Integer.parseInt(tagLimitString);

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();

        List<Answer> answersFromDB = answerDAO.select(em);
        ArrayList<TempAnswer> answers = new ArrayList<>();

        // Go through all answers in the DB
        for (Answer currentDBAnswer : answersFromDB) {
            // Create a new tempAnswer
            TempAnswer tempAnswer = addTagsToTempAnswer(currentDBAnswer, tagLimit, em);
            tempAnswer.setUsefulness(answerDAO.getAverageUsefulness(currentDBAnswer.getAnswerID(), em));

            if (currentDBAnswer.getAnswerType().canBeUserMade()) {
                answers.add(tempAnswer);
            }
        }

        em.getTransaction().commit();
        em.close();

        // Build the JSON object
        return Response.ok().entity(generateJSONResponseAllAnswers(answers)).build();
    }

    /**
     * @param answer   the answer to add the tags to
     * @param tagLimit the max amount of tags
     * @param em       the entitymanager
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private TempAnswer addTagsToTempAnswer(Answer answer, int tagLimit, EntityManager em) {
        TempAnswer tempAnswer = new TempAnswer(answer);

        if (tempAnswer.getAnswer().getAnswerType().isGroupedTags()) {
            TypeTagDAO typeTagDAO = new TypeTagDAO();
            List<TypeTag> typeTags = typeTagDAO.selectByType(tempAnswer.getAnswer().getAnswerType(), tagLimit, em);

            for (TypeTag typeTag : typeTags) {
                TempTag tempTag = new TempTag(typeTag.getTag(), tempAnswer.getAnswer().getAnswerID(), typeTag.getUpvotes(), typeTag.getDownvotes(), typeTag.getUsages());
                tempAnswer.addTag(tempTag);
            }
        } else {
            ResultDAO resultDAO = new ResultDAO();
            List<Result> resultsFromDB = resultDAO.selectByAnswer(tempAnswer.getAnswer().getAnswerID(), tagLimit, em);
            // Go through all results in the DB
            for (Result currentDBResult : resultsFromDB) {
                // If the current result contains the same answer as the tempanswer, add tag to tempanswer
                if (currentDBResult.getAnswer().getAnswerID() == tempAnswer.getAnswer().getAnswerID()) {
                    TempTag tempTag = new TempTag(currentDBResult.getTag(), tempAnswer.getAnswer().getAnswerID(),
                            currentDBResult.getUpvotes(), currentDBResult.getDownvotes(), currentDBResult.getUsages());
                    tempAnswer.addTag(tempTag);
                }
            }
        }
        return tempAnswer;
    }

    /**
     * service to get all tags with their values
     *
     * @return return response with all tags and their upvotes/downvotes/usages etc
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allTags")
    public Response getAllTags() {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempTag> tags = getAllTagsWithExtraInformationsByType(null, em);

        em.getTransaction().commit();
        em.close();

        // Build the JSON object
        return Response.ok().entity(generateJSONResponseAllTags(tags)).build();
    }

    /**
     * returns all tags with a specific type from the table typedTags as simple strings
     *
     * @param typeValueString the type as a string, needs to be a number (the ordinal of the enum type)
     * @return all tags as simple string, no extra informations
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allTypeTagsByType")
    public Response getAllTypedTagsByType(@QueryParam("type") String typeValueString) {
        AnswerType type = AnswerType.getValues()[Integer.parseInt(typeValueString)];
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        TypeTagDAO typeTagDAO = new TypeTagDAO();
        // Get all typed tags by type
        List<TypeTag> typeTags = typeTagDAO.selectByType(type, em);

        List<Tag> tags = new ArrayList<>();
        for (TypeTag typeTag : typeTags) {
            // add all tags to array
            tags.add(typeTag.getTag());
        }

        em.getTransaction().commit();
        em.close();
        // return json with all tags as simple string, no extra informations
        return Response.ok().entity(generateJSONResponseAutoCompleteList(tags)).build();
    }

    /**
     * @param typeValueString
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allTagsByTypeWithInfos")
    public Response getAllTagsByTypeWithInfos(@QueryParam("type") String typeValueString) {
        AnswerType type = null;
        if (typeValueString != null) {
            try {
                type = AnswerType.getValues()[Integer.parseInt(typeValueString)];
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempTag> tempTags = new ArrayList<>(getAllTagsWithExtraInformationsByType(type, em));

        em.getTransaction().commit();
        em.close();
        return Response.ok().entity(generateJSONResponseAllTags(tempTags)).build();
    }

    /**
     * service to get all blacklistentries
     *
     * @return return response with all BlackListEntries
     * @author Magnus Goetz
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allBlackList")
    public Response getAllBlackList() {
        BlacklistEntryDAO blacklistEntryDAO = new BlacklistEntryDAO();

        List<BlacklistEntry> blacklistEntries = blacklistEntryDAO.select();
        return Response.ok().entity(generateJSONResponseAllBlackList(blacklistEntries)).build();
    }

    /**
     * service to get all default wuestions
     *
     * @return return response with all default question
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allDefaultQuestion")
    public Response getAllDefaultQuestion() {
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();

        List<DefaultQuestion> defaultQuestion = defaultQuestionDAO.select();
        return Response.ok().entity(generateJSONResponseAllDefaultQuestion(defaultQuestion)).build();
    }

    /**
     * service to get all matches
     *
     * @return return response with all Matches
     * @author Magnus Goetz
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/allMatches")
    public Response getAllMatches() {
        MatchDAO matchDAO = new MatchDAO();

        List<Match> matches = matchDAO.select();
        return Response.ok().entity(generateJSONResponseAllMatches(matches)).build();
    }

    /**
     * service to get random default questions with a set amount
     *
     * @param amountQuestionsString
     * @return a list containing random questions in a random order
     * @author Tim Irmler
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/randomDefaultQuestion")
    public String defaultQuestionRandom(@QueryParam("amountQuestions") String amountQuestionsString) {
        final int max = Integer.parseInt(amountQuestionsString);
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();
        List<DefaultQuestion> defaultQuestionList = defaultQuestionDAO.selectRandom(max);
        DefaultQuestion[] defaultQuestions = new DefaultQuestion[defaultQuestionList.size()];

        for (int i = 0; i < defaultQuestionList.size(); i++) {
            defaultQuestions[i] = defaultQuestionList.get(i);
        }

        StringBuilder defaultQuestion = new StringBuilder("{\"defaultQuestion\":[");

        for (int i = 0; i < defaultQuestions.length; i++) {
            DefaultQuestion dQ = defaultQuestions[i];
            defaultQuestion.append("\"").append(dQ.getDefaultQuestion()).append("\"");
            if (i < defaultQuestions.length - 1) defaultQuestion.append(", ");
        }

        defaultQuestion.append("]}");

        return defaultQuestion.toString();
    }

    /**
     * Service to get a list of suggested Questions
     *
     * @param amountQuestionsString
     * @return a List of a limited amount of suggested Questions
     * @author Tim Irmler
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/questionSuggestions")
    public String questionSuggestions(@QueryParam("amountQuestions") String amountQuestionsString) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();

        final int amountQuestions = Integer.parseInt(amountQuestionsString);
        List<TempAnsweredQuestionTimesResult> answeredQuestions = answeredQuestionTimesResultDAO.selectMonthlyOrderedByUpvotes(new StatistikTimes(new Date()), amountQuestions);

        String[] answeredQuestionsString = new String[amountQuestions];
        if (answeredQuestions != null && answeredQuestions.size() > 0) {
            printDebug("answeredQuestions from DB size", answeredQuestions.size());
            for (int i = 0; i < answeredQuestions.size(); i++) {
                answeredQuestionsString[i] = answeredQuestions.get(i).getAnsweredQuestionTimesResult().getAnsweredQuestionResult().getAnsweredQuestion().getQuestion();
            }
        } else {
            printDebug("answeredQuestions from DB size", 0);
        }

        // count how many filler questions we need
        int fillerAmount = 0;
        for (int i = 0; i < amountQuestions; i++) {
            if (answeredQuestionsString[i] == null) {
                fillerAmount++;
            }
        }

        // if we need fillers, select as many random questions as needed and fill the empty spots with them
        if (fillerAmount > 0) {
            DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();
            List<DefaultQuestion> defaultQuestionList = defaultQuestionDAO.selectRandom(fillerAmount);
//            
            for (int i = 0; i < amountQuestions; i++) {
                if (answeredQuestionsString[i] == null) {
                    answeredQuestionsString[i] = defaultQuestionList.get(0).getDefaultQuestion();
                    defaultQuestionList.remove(0);
//                    
                }
            }
        }

        StringBuilder questionButton = new StringBuilder("{\"questions\":[");

        for (int i = 0; i < answeredQuestionsString.length; i++) {
            String q = answeredQuestionsString[i];
            questionButton.append("\"").append(q).append("\"");
            if (i < answeredQuestionsString.length - 1) questionButton.append(", ");
        }

        questionButton.append("]}");

        printDebug("question suggestion json", questionButton.toString(), 1, 1);

        return questionButton.toString();
    }

    /**
     * @param amountQuestionsString
     * @return
     * @author Tim Irmler
     * @since 16.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/topQuestionSuggestions")
    public String topQuestionSuggestions(@QueryParam("amountQuestions") String amountQuestionsString) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();

        final int amountQuestions = Integer.parseInt(amountQuestionsString);
        List<TempAnsweredQuestionTimesResult> answeredQuestionsTimesResult = answeredQuestionTimesResultDAO.selectMonthlyOrderedByUpvotes(new StatistikTimes(new Date()), amountQuestions);

        ArrayList<TempAnsweredQuestion> answeredQuestions = new ArrayList<>();
        for (TempAnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionsTimesResult) {
            answeredQuestions.add(new TempAnsweredQuestion(answeredQuestionTimesResult));
        }

        String json = buildAnsweredQuestionJsonResponse(answeredQuestions);

        return json;
    }

    /**
     * @param questionID
     * @return
     * @author Tim Irmler
     * @since 20.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/singleAnsweredQuestion")
    public String singleAnsweredQuestion(@QueryParam("questionID") long questionID) {
        boolean withAnswers = true;

        String json = null;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestion answeredQuestion = answeredQuestionDAO.select(questionID, em);
        if (answeredQuestion != null) {
            AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
            List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = answeredQuestionTimesResultDAO.selectByQuestion(questionID, em);

            ArrayList<TempAnswer> tempAnswers = new ArrayList<>();
            for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                Answer answer = answeredQuestionTimesResult.getAnsweredQuestionResult().getResult().getAnswer();
                TempAnswer tempAnswer = new TempAnswer(answer);
                tempAnswer.setUpvotes(answeredQuestionTimesResult.getUpvote());
                tempAnswer.setDownvotes(answeredQuestionTimesResult.getDownvote());

                boolean exists = false;
                for (TempAnswer tempAnswer1 : tempAnswers) {
                    if (tempAnswer.getAnswer().getAnswerID() == tempAnswer1.getAnswer().getAnswerID()) {
                        tempAnswer1.increaseUpvotes(tempAnswer.getUpvotes());
                        tempAnswer1.increaseDownvotes(tempAnswer.getDownvotes());
                        tempAnswer1.setUsefulness(CalculateRating.getRating(tempAnswer1.getUpvotes(), tempAnswer1.getDownvotes()));

                        exists = true;
                        printDebug("answered question already exists, increasing values", tempAnswer);
                        break;
                    }
                }

                if (!exists) {
                    tempAnswer.setUsefulness(CalculateRating.getRating(tempAnswer.getUpvotes(), tempAnswer.getDownvotes()));

                    tempAnswers.add(tempAnswer);

                    printDebug("answered question not already exists, adding it", tempAnswer);
                }
            }

            TempAnsweredQuestion tempAnsweredQuestion = new TempAnsweredQuestion(answeredQuestion, tempAnswers);

            json = buildSingleAnsweredQuestionJsonResponse(tempAnsweredQuestion, true);
        }

        em.getTransaction().commit();
        em.close();

        return json;
    }

    /**
     * @param timeRangeString
     * @param withAnswersString
     * @return
     * @author Tim Irmler
     * @since 18.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allAnsweredQuestions")
    public String allAnsweredQuestions(@QueryParam("timeRange") String timeRangeString, @QueryParam("withAnswers") String withAnswersString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        boolean withAnswers = Boolean.parseBoolean(withAnswersString);
        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        List<TempAnsweredQuestionTimesResult> answeredQuestionTimesResults = answeredQuestionTimesResultDAO.selectByTimeGroupedByQuestion(userID, time, null);

        if (answeredQuestionTimesResults != null) {
            ArrayList<TempAnsweredQuestion> answeredQuestions = new ArrayList<>();
            for (TempAnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                answeredQuestions.add(new TempAnsweredQuestion(answeredQuestionTimesResult));
            }

            String json;
            if (withAnswers) {
                json = null;
            } else {
                json = buildAnsweredQuestionJsonResponse(answeredQuestions);
            }
            return json;
        }

        return "[]";
    }

    /**
     * @param answerID
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allAnsweredQuestionsPerAnswer")
    public String allAnsweredQuestions(@QueryParam("answerID") long answerID) {
        AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
        List<TempAnsweredQuestionTimesResult> answeredQuestionTimesResults = answeredQuestionTimesResultDAO.selectByAnswer(answerID);

        String json = buildAnsweredQuestionPerAnswerJsonResponse(answeredQuestionTimesResults);

        return json;
    }

    /**
     * @param timeRangeString
     * @return
     * @author Tim Irmler
     * @since 19.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allUnansweredQuestions")
    public String allUnansweredQuestions(@QueryParam("timeRange") String timeRangeString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        UnansweredQuestionStatistikDAO unansweredQuestionStatistikDAO = new UnansweredQuestionStatistikDAO();
        List<TempUnansweredQuestion> unansweredQuestions = unansweredQuestionStatistikDAO.selectByTimeGroupedByQuestion(userID, time, null);

        String json = buildUnansweredQuestionJsonResponse(unansweredQuestions);

        return json;
    }

    /**
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status")
    public Response getStatus() {
        StringBuilder json = new StringBuilder("{ \"status\": ");

        boolean success = true;
        try {
            questionSuggestions("3");
            IntentFinderNew intentFinder = new IntentFinderNew();
            intentFinder.search("demo", false);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        json.append(success);

        json.append(" }");

        return Response.status(200).entity(json.toString()).build();
    }

    /**
     * Service to get random Questions from the Database
     *
     * @return a JSON Object with a list of random Questions from the Database
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/randomquestion")
    public String run() {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();

        List<AnsweredQuestion> answeredQuestions = answeredQuestionDAO.select();
        final int maxQuestions = 3;

        StringBuilder allQuestion = new StringBuilder();
        ArrayList<Integer> numbers = new ArrayList<>();

        allQuestion.append("{\"questions\":[");
        for (int i = 0; i < maxQuestions; i++) {
            Random r = new Random();
            int randomIndex;
            do {
                randomIndex = r.nextInt(maxQuestions);
            } while (numbers.contains(randomIndex));
            numbers.add(randomIndex);

            AnsweredQuestion a = answeredQuestions.get(randomIndex);
            allQuestion.append("\"").append(a.getQuestion()).append("\"");
            if (i < maxQuestions - 1) allQuestion.append(", ");
        }

        allQuestion.append("]}");


        return allQuestion.toString();
    }

    /**
     * Service to get all Tags in the Database
     *
     * @return a JSON Object with a List of all Tags in the Database as simple string (just the name of the tags)
     * @author Marc
     * @since 17.07.2021
     */
    @GET
    @Path("/autocompleteList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find() {
        TagDAO tagDAO = new TagDAO();

        List<Tag> tags = tagDAO.select();

        String json = "{ \"suggestions\":" + generateJSONResponseAutoCompleteList(tags) + "}";

        return Response.ok().entity(json).build();
    }

    /**
     * Service to get all files from the Database
     *
     * @return JSON Object with all Files
     * @author Marc
     * @since 17.07.2021
     */
    @GET
    @Path("/files")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFiles() {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        UploadFileDAO fileDAO = new UploadFileDAO();
        List<UploadFile> files = fileDAO.select(em);

        String json = "{\"files\":" + generateJSONResponseAllFiles(files) + "}";

        em.getTransaction().commit();
        em.close();
        return Response.status(200).entity(json).build();
    }

    /**
     * @return all answer types
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @GET
    @Path("/answerTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response answerTypes() {
        StringBuilder json = new StringBuilder("{\"answerTypes\":");
        json.append(generateJSONResponseAnswerTypes(null));
        json.append("}");

        return Response.status(200).entity(json.toString()).build();
    }

    /**
     * @param id
     * @return
     * @author Marc Andri Fuchs
     * @since 22.07.2021
     */
    @GET
    @Path("/singleBlacklistEntry")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleBlacklistEntry(@QueryParam("id") long id) {
        BlacklistEntryDAO entryDAO = new BlacklistEntryDAO();

        BlacklistEntry entry = entryDAO.select(id);

        JSONParser parser = new JSONParser();

        try {

            return Response.status(200).entity(parser.objectToJSON(entry)).build();
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }


        return Response.status(501).entity("Object couldn't be parsed!").build();
    }

    /**
     * Generates a JSON String for all Files
     *
     * @param files the list of all files
     * @return the JSON String
     * @author Marc
     * @since 17.07.2021
     */
    private String generateJSONResponseAllFiles(List<UploadFile> files) {
        JSONParser parser = new JSONParser();
        String json = null;
        try {
            json = parser.listToJSON(files, UploadFile.class, ParserResponseType.LIST);
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * @param neededAnswerType
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private String generateJSONResponseAnswerTypes(AnswerType neededAnswerType) {
        AnswerType[] answerTypes;
        if (neededAnswerType == null || !neededAnswerType.canBeUserMade()) {
            answerTypes = AnswerType.values();
        } else {
            answerTypes = new AnswerType[1];
            answerTypes[0] = neededAnswerType;
        }

        StringBuilder json = new StringBuilder("[");
        for (AnswerType answerType : answerTypes) {
            if (answerType.canBeUserMade()) {
                json.append("{");
                json.append("\"name\":\"").append(answerType.getName()).append("\"");
                json.append(",");
                json.append("\"value\":").append(answerType.ordinal());
                json.append(",");
                json.append("\"groupedTags\":").append(answerType.isGroupedTags());
                json.append(",");
                json.append("\"hidden\":").append(answerType.isHidden());
                json.append(",");
                json.append("\"forceHidden\":").append(answerType.isForceHidden());
                json.append("}");
                json.append(",");
            }
        }
        json = new StringBuilder(json.substring(0, json.length() - 1)); // remove the last comma
        json.append("]");
        return json.toString();
    }

    /**
     * Builds a JSON Object of all tag Strings
     *
     * @param tags the list of all Tags
     * @return the JSON Object Stringified
     * @author Marc
     * @since 17.07.2021
     */
    private String generateJSONResponseAutoCompleteList(List<Tag> tags) {
        StringBuilder sb = new StringBuilder("[");

        Iterator<Tag> iter = tags.iterator();
        while (iter.hasNext()) {
            Tag t = iter.next();
            sb.append("\"").append(t.getTag()).append("\"");
            if (iter.hasNext()) sb.append(", ");
        }

        sb.append("]");

        printDebug("autocomplete list", sb);
        return sb.toString();
    }

    /**
     * Generates the JSON Response for a List of multiple Answers
     *
     * @param answers the List of Answers
     * @return the JSON Object containing the List of Answers
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private String generateJSONResponseAllAnswers(List<TempAnswer> answers) {
        StringBuilder json = new StringBuilder("[");

        Iterator<TempAnswer> answerIterator = answers.iterator();
        while (answerIterator.hasNext()) {
            TempAnswer answer = answerIterator.next();

            String tagJSON = "\"tags\":" + generateJSONResponseAllTags(answer.getTags());
            String fileJSON = "\"files\":" + generateJSONResponseAllFiles(answer.getAnswer().getFiles());
            String answerTypeJSON = "\"answerType\":" + generateJSONResponseAnswerTypes(answer.getAnswer().getAnswerType());
            AnswerDAO answerDAO = new AnswerDAO();
            Long answerAskedAmount = answerDAO.countAskedAmount(answer.getAnswer().getAnswerID());

            String answerJSON = "{" +
                    "\"id\": \"" + answer.getAnswer().getAnswerID() + "\"," +
                    "\"title\": \"" + answer.getAnswer().getTitle() + "\"," +
                    "\"answer\": \"" + answer.getAnswer().getAnswer() + "\"," +
                    "\"views\": \"" + answerAskedAmount + "\"," +
                    "\"isHidden\": \"" + answer.getAnswer().isHidden() + "\"," +
                    "\"averageUsefulness\": \"" + answer.getUsefulness() + "\"," +
                    answerTypeJSON + "," +
                    tagJSON + "," +
                    fileJSON +
                    "}";
            if (answerIterator.hasNext())
                answerJSON += ",";

            json.append(answerJSON);
        }

        json.append("]");

        return json.toString();
    }

    /**
     * Generates a JSON Object with a List of Tags
     *
     * @param tags the List of Tags
     * @return the JSON Object containing a List of Tags
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public String generateJSONResponseAllTags(List<TempTag> tags) {
        JSONParser parser = new JSONParser();

        String json = null;
        try {
            json = parser.listToJSON(tags, TempTag.class, ParserResponseType.LIST);
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }

        return json;
    }

    private String generateJSONResponseSingleTag(TempTag tempTag) {
        if (tempTag == null) {
            return null;
        }
        StringBuilder tagJson = new StringBuilder("{");

        tagJson.append("\"upvotes\": ").append(tempTag.getUpvotes()).append(",").append("\"downvotes\": ").append(tempTag.getDownvotes()).append(",").append("\"usage\": ")
                .append(tempTag.getTagUsages()).append(",").append("\"amountAnswers\": ").append(tempTag.getAmountAnswers()).append(",").append("\"tag\": {\"id\":").append(tempTag.getTag().getTagID())
                .append(",\"tag\":\"").append(tempTag.getTag().getTag()).append("\"},").append("\"answers\":");

        StringBuilder allAnswersJson = new StringBuilder("[");

        Iterator<TempAnswer> answerIterator = tempTag.getAllAnswers().iterator();
        while (answerIterator.hasNext()) {
            TempAnswer answer = answerIterator.next();

            String answerTypeJSON = "\"answerType\":" + generateJSONResponseAnswerTypes(answer.getAnswer().getAnswerType());

            String answerJSON = generateSingleAnswerJson(answer, answerTypeJSON);
            if (answerIterator.hasNext())
                answerJSON += ",";

            allAnswersJson.append(answerJSON);
        }

        allAnswersJson.append("]");
        tagJson.append(allAnswersJson).append("}");

        printDebug("single tag", tagJson.toString());
        return tagJson.toString();
    }

    /**
     * @param tempAnswers
     * @param listName
     * @return
     * @author Tim Irmler
     * @since 20.08.2021
     */
    private String generateAllAnswersJson(List<TempAnswer> tempAnswers, String listName) {
        if (listName == null) {
            listName = "answers";
        }
        StringBuilder json = new StringBuilder("\"" + listName + "\":[");

        Iterator<TempAnswer> tempAnswerIterator = tempAnswers.iterator();
        while (tempAnswerIterator.hasNext()) {
            TempAnswer tempAnswer = tempAnswerIterator.next();
            String answerTypeJSON = "\"answerType\":" + generateJSONResponseAnswerTypes(tempAnswer.getAnswer().getAnswerType());

            json.append(generateSingleAnswerJson(tempAnswer, answerTypeJSON));

            if (tempAnswerIterator.hasNext()) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }

    /**
     * @param answer
     * @param answerTypeJSON
     * @return
     * @author Tim Irmler
     * @since 20.08.2021
     */
    private String generateSingleAnswerJson(TempAnswer answer, String answerTypeJSON) {
        AnswerDAO answerDAO = new AnswerDAO();
        Long answerAskedAmount = answerDAO.countAskedAmount(answer.getAnswer().getAnswerID());

        return "{" +
                "\"id\": \"" + answer.getAnswer().getAnswerID() + "\"," +
                "\"title\": \"" + answer.getAnswer().getTitle() + "\"," +
                "\"views\": \"" + answerAskedAmount + "\"," +
                "\"isHidden\": \"" + answer.getAnswer().isHidden() + "\"," +
                "\"averageUsefulness\": \"" + answer.getUsefulness() + "\"," +
                "\"upvotes\": " + answer.getUpvotes() + "," +
                "\"downvotes\": " + answer.getDownvotes() + "," +
                answerTypeJSON +
                "}";
    }

    /**
     * Generates a JSON Object with a List of blacklistEntries
     *
     * @param blacklistEntries the List of blacklistEntries
     * @return the JSON Object containing a List of blacklistEntries
     * @author Magnus
     * @since 17.07.2021
     */
    private String generateJSONResponseAllBlackList(List<BlacklistEntry> blacklistEntries) {
        JSONParser parser = new JSONParser();

        String json = null;
        try {
            json = parser.listToJSON(blacklistEntries, BlacklistEntry.class, ParserResponseType.LIST);
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }

        printDebug("all blacklist", json);
        return json;
    }

    /**
     * Generates a JSON Object with a List of blacklistEntries
     *
     * @param matches the List of matches
     * @return the JSON Object containing a List of blacklistEntries
     * @author Magnus
     * @since 17.07.2021
     */
    private String generateJSONResponseAllMatches(List<Match> matches) {
        StringBuilder json = new StringBuilder("[");
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match currentMatch = matchIterator.next();
            String matchString = "{";

            String statusString;
            if (CalculateRating.isBadMatch(currentMatch.getUpvote(), currentMatch.getDownvote())) {
                statusString = "Wird nicht übersetzt";
            } else {
                statusString = "Wird übersetzt";
            }

            String tagString = "{\"id\":" + currentMatch.getTag().getTagID() + ",\"tag\":\"" + currentMatch.getTag().getTag() + "\"}";

            matchString += "\"word\":\"" + currentMatch.getWord() + "\",";
            matchString += "\"id\":" + currentMatch.getMatchID() + ",";
            matchString += "\"upvote\":" + currentMatch.getUpvote() + ",";
            matchString += "\"downvote\":" + currentMatch.getDownvote() + ",";
            matchString += "\"Status\":\"" + statusString + "\",";

            matchString += "\"tag\":" + tagString;

            matchString += "}";

            if (matchIterator.hasNext()) {
                matchString += ",";
            }

            json.append(matchString);
        }

        json.append("]");

        printDebug("all matches", json);
        return json.toString();
    }

    /**
     * generate the JSON string for all default question
     *
     * @param defaultQuestions
     * @return the JSON string
     * @author Sarah Ambi
     * @since 17.07.2021
     */
    private String generateJSONResponseAllDefaultQuestion(List<DefaultQuestion> defaultQuestions) {
        JSONParser parser = new JSONParser();

        String json = null;
        try {
            json = parser.listToJSON(defaultQuestions, DefaultQuestion.class, ParserResponseType.LIST);
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }

        printDebug("all default questions", json);
        return json;
    }

    /**
     * Method for checking if array list already contains an answer with this id.
     *
     * @param arrayList the array list containing all the answers to check
     * @param id        the id we want to check if it already is in the array list
     * @return boolean true if id is in array, false if id is not in array
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private boolean listContainsAnswerID(List<TempAnswer> arrayList, long id) {
        for (TempAnswer answer : arrayList) {
            if (answer.getAnswer().getAnswerID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for checking if array list already contains an tag with this id.
     *
     * @param arrayList the array list containing all the tags to check
     * @param id        the id we want to check if it already is in the array list
     * @return true if id is in array, false if id is not in array
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private Integer listContainsTagID(List<TempTag> arrayList, long id) {
        if (arrayList.size() > 0) {
            int index = 0;
            for (TempTag tag : arrayList) {
                if (tag.getTag().getTagID() == id) {
                    return index;
                }
                index++;
            }
        }

        return null;
    }

    /**
     * @param type
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private List<TempTag> getAllTagsWithExtraInformationsByType(AnswerType type, EntityManager em) {
        if (type == null) { // If type is null, we want ALL tags. this means we can (and definetely will) get duplicates which we need to merge together
            List<TempTag> tempTagsWithResult = getAllTagsWithExtraInformationsFromResultByType(null, em);
            List<TempTag> tempTagsWithGroupedType = getAllTagsWithExtraInformationsFromGroupedTypeTagByType(null, em);

            List<TempTag> tempTags = new ArrayList<>();

            // "merge" all duplicate tags into one
            printDebug("merge duplicate tags", "merging all result tags");
            mergeDuplicateTags(tempTagsWithResult, tempTags);

            // "merge" all duplicate tags into one
            printDebug("merge duplicate tags", "merging all type tags");
            mergeDuplicateTags(tempTagsWithGroupedType, tempTags);

            // Get all the tags that don't have an answer and don't have a special type!
            TagDAO tagDAO = new TagDAO();
            List<Tag> noAnswerTags = tagDAO.selectNoAnswerTag(em);
            for (Tag tag : noAnswerTags) {
                tempTags.add(new TempTag(tag));
            }

            return tempTags;
        } else if (type.isGroupedTags()) { // We only want the tags with a type
            return getAllTagsWithExtraInformationsFromGroupedTypeTagByType(type, em);
        } else { // We only want tags that have a result
            return getAllTagsWithExtraInformationsFromResultByType(type, em);
        }
    }

    /**
     * @param tagID
     * @param type
     * @param em
     * @return
     * @author Tim Irmler
     * @since 27.07.2021
     */
    private TempTag getSingleTagWithExtraInformations(long tagID, AnswerType type, boolean withAnswers, EntityManager em) {
        TempTag tempTag = new TempTag();
        TempTag tempTagTypeTag = getSingleTagWithExtraInformationsFromGroupedTypeTagByType(tagID, type, withAnswers, em);
        TempTag tempTagResult = getSingleTagWithExtraInformationsFromResultByType(tagID, type, withAnswers, em);

        if (tempTagResult != null && tempTagTypeTag != null) {
            // if the tag has result entries and type tag entries
            tempTag.setTag(tempTagTypeTag.getTag());
            tempTag.increaseAllValues(tempTagTypeTag);
            tempTag.increaseAllValues(tempTagResult);
            tempTag.setAllAnswers(tempTagTypeTag.getAllAnswers());
            tempTag.addAnswers(tempTagResult.getAllAnswers());
        } else if (tempTagResult != null) {
            // if the tag has no answer in result
            tempTag.setTag(tempTagResult.getTag());
            tempTag.increaseAllValues(tempTagResult);
            tempTag.setAllAnswers(tempTagResult.getAllAnswers());
        } else if (tempTagTypeTag != null) {
            // If the tag has no answers with a specific grouped type
            tempTag.setTag(tempTagTypeTag.getTag());
            tempTag.increaseAllValues(tempTagTypeTag);
            tempTag.setAllAnswers(tempTagTypeTag.getAllAnswers());
        } else {
            // if the tag has no answers
            TagDAO tagDAO = new TagDAO();
            tempTag = new TempTag(tagDAO.select(tagID, em));
        }
        return tempTag;
    }

    /**
     * method to check if list tempTagsToMergeIn already contains a tag from list tempTagsToCheck
     * if tag already exists, merge the tag and its values together
     *
     * @param tempTagsToCheck
     * @param tempTagsToMergeIn
     * @author Tim Irmler
     * @since 19.07.2021
     */
    private void mergeDuplicateTags(List<TempTag> tempTagsToCheck, List<TempTag> tempTagsToMergeIn) {
        printDebug("new method", "Get.mergeDuplicateTags");
        printDebug("new method parameters", "tempTagsToCheck = " + tempTagsToCheck + ", tempTagsToMergeIn = " + tempTagsToMergeIn);
        for (TempTag tempTag : tempTagsToCheck) {
            printDebug("current tempTag to check", tempTag.toString());
            Integer alreadyExistingTagIndex = listContainsTagID(tempTagsToMergeIn, tempTag.getTag().getTagID());
            if (alreadyExistingTagIndex == null) {
                printDebug("tag doesnt already exist", "adding tag '" + tempTag + "' to array");
                tempTagsToMergeIn.add(tempTag);
            } else {
                TempTag tempTagToUpdate = tempTagsToMergeIn.get(alreadyExistingTagIndex);
                printDebug("tag already exists", "updating values of tag '" + tempTagToUpdate + "'");
                tempTagToUpdate.increaseAllValues(tempTag);
                tempTagToUpdate.addAnswers(tempTag.getAllAnswers());
            }
        }
    }

    /**
     * get one specific tag with a specific type and all its additional informations
     *
     * @param tagID
     * @param type
     * @param em
     * @return
     * @author Tim Irmler
     * @since 27.07.2021
     */
    private TempTag getSingleTagWithExtraInformationsFromGroupedTypeTagByType(long tagID, AnswerType type, boolean withAnswers, EntityManager em) {
        printDebug("New method", "Get.getSingleTagWithExtraInformationsFromGroupedTypeTagByType");
        printDebug("params of new method", "tagID = " + tagID + ", type = " + type + ", withAnswers = " + withAnswers);
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        AnswerDAO answerDAO = new AnswerDAO();

        TempTag tempTag;
        if (type == null) {
            List<TypeTag> typeTags = typeTagDAO.selectByTag(tagID, em);
            if (typeTags != null && typeTags.size() > 0) {
                tempTag = new TempTag(typeTags.get(0).getTag());

                ArrayList<AnswerType> typesThisTagUses = new ArrayList<>();
                for (TypeTag typeTag : typeTags) {
                    printDebug("Current typeTag", typeTag.toString());

                    if (withAnswers) {
                        if (!typesThisTagUses.contains(typeTag.getAnswerType())) {
                            typesThisTagUses.add(typeTag.getAnswerType());
                        }
                    }
                    int amountAnswers = answerDAO.countByTypeAndTag(tagID, typeTag.getAnswerType(), em);

                    tempTag.increaseAmountAnswers(amountAnswers);
                    tempTag.increaseUpvotes(typeTag.getUpvotes());
                    tempTag.increaseDownvotes(typeTag.getDownvotes());
                    tempTag.increaseTagUsages(typeTag.getUsages());
                }

                if (withAnswers) {
                    for (AnswerType answerType : typesThisTagUses) {
                        List<Answer> answers = answerDAO.selectByTypeAndTag(tagID, answerType, em);
                        for (Answer answer : answers) {
                            TypeTag result = typeTagDAO.selectByTagAndType(answerType, tempTag.getTag().getTagID(), em);
                            setUpAndDownvote(em, answerDAO, tempTag, result, answer);
                        }
                    }
                }
            } else {
                return null;
            }
        } else {
            TypeTag typeTag = typeTagDAO.selectByTagAndType(type, tagID, em);
            if (typeTag != null) {
                int amountAnswers = answerDAO.countByTypeAndTag(tagID, type, em);

                tempTag = new TempTag(typeTag.getTag());
                tempTag.setAmountAnswers(amountAnswers);
                tempTag.setUpvotes(typeTag.getUpvotes());
                tempTag.setDownvotes(typeTag.getDownvotes());
                tempTag.setTagUsages(typeTag.getUsages());
                if (withAnswers) {
                    List<Answer> answers = answerDAO.selectByTypeAndTag(tagID, type, em);
                    setUpAndDownvote(em, answerDAO, tempTag, typeTag, answers);
                }
            } else {
                return null;
            }
        }
        return tempTag;
    }

    /**
     * @param type
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private List<TempTag> getAllTagsWithExtraInformationsFromGroupedTypeTagByType(AnswerType type, EntityManager em) {
        printDebug("New Method", "Get.getAllTagsWithExtraInformationsFromGroupedTypeTagByType");
        printDebug("parameter of new method", "type = " + type);

        TypeTagDAO typeTagDAO = new TypeTagDAO();
        List<TypeTag> typeTags;

        int amountAnswersWithType = 0;
        AnswerDAO answerDAO = new AnswerDAO();
        if (type == null) {
            typeTags = typeTagDAO.select(em);
        } else {
            amountAnswersWithType = answerDAO.countByType(type, em);
            typeTags = typeTagDAO.selectByType(type, em);
        }

        

        ArrayList<TempTag> tags = new ArrayList<>();
        for (TypeTag typeTag : typeTags) {
            printDebug("current typeTag", typeTag.toString());
            boolean newTypeTag = true;
            if (type == null) { // if type is null, we want ALL grouped tags. but this means we could get duplicates, so we check if we have to increase the values of an already existing typeTag or if we have to create a new one
                amountAnswersWithType = answerDAO.countByType(typeTag.getAnswerType(), em);
                Integer alreadyExistingTagIndex = listContainsTagID(tags, typeTag.getTag().getTagID());
                if (alreadyExistingTagIndex != null) { // If the tag is already in the array, update values and not create a new type tag
                    TempTag tagToUpdate = tags.get(alreadyExistingTagIndex);
                    tagToUpdate.increaseUpvotes(typeTag.getUpvotes());
                    tagToUpdate.increaseDownvotes(typeTag.getDownvotes());
                    tagToUpdate.increaseTagUsages(typeTag.getUsages());
                    newTypeTag = false;
                }
            }
            if (newTypeTag) {
                TempTag tempTag = new TempTag(typeTag.getTag());
                tempTag.setAmountAnswers(amountAnswersWithType);
                tempTag.setUpvotes(typeTag.getUpvotes());
                tempTag.setDownvotes(typeTag.getDownvotes());
                tempTag.setTagUsages(typeTag.getUsages());

                tags.add(tempTag);
            }
        }
        return tags;
    }

    /**
     * get single result with specified type and tag
     *
     * @param tagID
     * @param type
     * @param withAnswers
     * @param em
     * @return
     * @author Tim Irmler
     * @since 27.07.2021
     */
    private TempTag getSingleTagWithExtraInformationsFromResultByType(long tagID, AnswerType type, boolean withAnswers, EntityManager em) {
        printDebug("new method", "Get.getSingleTagWithExtraInformationsFromResultByType");
        printDebug("parameter of new method", "tagID = " + tagID + ", type = " + type + ", withAnswers = " + withAnswers);
        ResultDAO resultDAO = new ResultDAO();
        AnswerDAO answerDAO = new AnswerDAO();
        TempTag tempTag;

        if (type == null) {

            List<Result> results = resultDAO.selectByTag(tagID, em);
            if (results != null && results.size() > 0) {
                tempTag = new TempTag(results.get(0).getTag());

                ArrayList<AnswerType> typesThisTagUses = new ArrayList<>();
                for (Result result : results) {
                    tempTag.increaseUpvotes(result.getUpvotes());
                    tempTag.increaseDownvotes(result.getDownvotes());
                    tempTag.increaseTagUsages(result.getUsages());

                    if (!typesThisTagUses.contains(result.getAnswer().getAnswerType())) {
                        typesThisTagUses.add(result.getAnswer().getAnswerType());
                    }
                }

                for (AnswerType answerType : typesThisTagUses) {
                    int amountAnswers = answerDAO.countByTypeAndTag(tagID, answerType, em);

                    tempTag.increaseAmountAnswers(amountAnswers);
                    if (withAnswers) {
                        List<Answer> answers = answerDAO.selectByTypeAndTag(tagID, answerType, em);
                        for (Answer answer : answers) {
                            Result result = resultDAO.selectByTagAndAnswer(answer.getAnswerID(), tempTag.getTag().getTagID(), em);
                            setUpAndDownvote(em, answerDAO, tempTag, result, answer);
                        }
                    }
                }
            } else {
                return null;
            }
        } else {
            List<Result> results = resultDAO.selectByTagAndType(tagID, type, em);
            tempTag = new TempTag();
            if (results != null) {
                boolean setUpAndDownvotes = false;
                for (Result result : results) {
                    if (tempTag.getTag().getTagID() != result.getTag().getTagID()) {
                        tempTag.setTag(result.getTag());
                    }

                    if (withAnswers && !setUpAndDownvotes) {
                        List<Answer> answers = answerDAO.selectByTypeAndTag(tagID, type, em);
                        setUpAndDownvote(em, answerDAO, tempTag, result, answers);
                        setUpAndDownvotes = true;
                    }
                }
                int amountAnswers = answerDAO.countByTypeAndTag(tagID, type, em);
                tempTag.setAmountAnswers(amountAnswers);
            } else {
                return null;
            }
        }

        return tempTag;
    }

    private void setUpAndDownvote(EntityManager em, AnswerDAO answerDAO, TempTag tempTag, ResultParent result, List<Answer> answers) {
        printDebug("new method", "Get.setUpAndDownvote");
        printDebug("new method params", "tempTag = " + tempTag + ", result = " + result + ", answers = " + answers);
        ArrayList<TempAnswer> tempAnswers = tempTag.getTempAnswersFromAnswers(answers);

        for (TempAnswer tempAnswer : tempAnswers) {
            tempAnswer.setUpvotes(result.getUpvotes());
            tempAnswer.setDownvotes(result.getDownvotes());
        }
        tempTag.addAnswersWithUsefulness(tempAnswers, answerDAO, em);
    }

    /**
     * @param em
     * @param answerDAO
     * @param tempTag
     * @param result
     * @param answer
     * @author Tim Irmler
     * @since 29.07.2021
     */
    private void setUpAndDownvote(EntityManager em, AnswerDAO answerDAO, TempTag tempTag, ResultParent result, Answer answer) {
        printDebug("new method", "Get.setUpAndDownVote");
        printDebug("new methods params", "tempTag = " + tempTag + ", result = " + result + ", answer = " + answer);

        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        setUpAndDownvote(em, answerDAO, tempTag, result, answers);
    }

    /**
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private List<TempTag> getAllTagsWithExtraInformationsFromResultByType(AnswerType type, EntityManager em) {
        printDebug("New Method", "Get.getAllTagsWithExtraInformationsFromResultByType");
        printDebug("parameter of new method", "type = " + type);

        ResultDAO resultDAO = new ResultDAO();
        AnswerDAO answerDAO = new AnswerDAO();

        List<Result> results;
        if (type == null) {
            results = resultDAO.select(em);
        } else {
            results = resultDAO.selectByType(type, em);
        }

        ArrayList<TempTag> tags = new ArrayList<>();
//        ArrayList<TempAnswer> tempAnswers = new ArrayList<>();
        printDebug("Get.getAllTagsWithExtraInformationsFromResultByType", "Adding all tags (without duplicates) to ArrayList by checking " + results.size() + " result sets...");
        for (Result result : results) {
            printDebug("Get.getAllTagsWithExtraInformationsFromResultByType", "current result set = " + result.getTag());

            Tag dbTag = result.getTag();
            TempTag tempTag = new TempTag(dbTag);
            Integer alreadyExistingTagIndex = listContainsTagID(tags, tempTag.getTag().getTagID());
            if (alreadyExistingTagIndex == null) {
                printDebug("Get.getAllTagsWithExtraInformationsFromResultByType", "Adding tag \"" + tempTag.getTag().getTag() + "\" to ArrayList tags because its new");
                tempTag.setUpvotes(result.getUpvotes());
                tempTag.setDownvotes(result.getDownvotes());
                tempTag.setTagUsages(result.getUsages());

                int amountAnswers = answerDAO.countByTypeAndTag(result.getTag().getTagID(), result.getAnswer().getAnswerType(), em);
                tempTag.setAmountAnswers(amountAnswers);

                tags.add(tempTag);
            } else {
                printDebug("Get.getAllTagsWithExtraInformationsFromResultByType", "Found duplicate tag: (" + tempTag.getTag().getTag() + ")");

                TempTag tagToUpdateValues;
                if (tags.get(alreadyExistingTagIndex).getTag().getTagID() == tempTag.getTag().getTagID()) {
                    tagToUpdateValues = tags.get(alreadyExistingTagIndex);
                    tagToUpdateValues.increaseUpvotes(result.getUpvotes());
                    tagToUpdateValues.increaseDownvotes(result.getDownvotes());
                    tagToUpdateValues.increaseTagUsages(result.getUsages());

                    printDebug("Get.getAllTagsWithExtraInformationsFromResultByType", "Updating values of tag \"" + tagToUpdateValues.getTag().getTag() + "\" {" +
                            "\n\tupvotes: " + tagToUpdateValues.getUpvotes() +
                            "\n\tdownvotes: " + tagToUpdateValues.getDownvotes() +
                            "\n\tusage: " + tagToUpdateValues.getTagUsages() +
                            "\n}");
                }
            }
        }

        return tags;
    }

    /**
     * Get all users
     *
     * @param request
     * @return json object with all user
     * @author Sarah Ambi
     * @since 02.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allUsers")
    public Response getAllUser(@Context HttpServletRequest request) {
        UserLoginDAO userLoginDAO = new UserLoginDAO();

        long loggedInUserID = (long) request.getSession().getAttribute("user");

        List<UserLogin> userlogin = userLoginDAO.select();
        return Response.ok().entity(generateJSONResponseAllUsers(userlogin, loggedInUserID)).build();
    }

    /**
     * Get a single user
     *
     * @param userID  the id of the user that we want
     * @param request
     * @return json object with single user
     * @author Sarah Ambi
     * @since 03.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/singleUser")
    public Response getSingleUser(@QueryParam("userID") long userID, @Context HttpServletRequest request) {
        long userLogged = (long) request.getSession().getAttribute("user");
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID, em);
        ArrayList<UserLogin> userLogins = new ArrayList<>();
        userLogins.add(user);

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity(generateJSONResponseAllUsers(userLogins, userLogged)).build();
    }

    /**
     * Generates the JSON Response for a List of multiple Users
     *
     * @param userLogins     the List of Users
     * @param loggedInUserID
     * @return the JSON Object containing the List of Users
     * @author Sarah Ambi
     * @since 02.08.2021
     */
    private String generateJSONResponseAllUsers(List<UserLogin> userLogins, long loggedInUserID) {
        StringBuilder json = new StringBuilder("[");

        Iterator<UserLogin> userLoginIterator = userLogins.iterator();
        while (userLoginIterator.hasNext()) {
            UserLogin userLogin = userLoginIterator.next();

            StatistikTimes statistikTimes = userLogin.getActualLastTimeLoggedIn();
            String lastTimeLoggedInString = "Noch nie";
            if (statistikTimes != null) {
                lastTimeLoggedInString = statistikTimes.getFormatted(true);
            }

            boolean isCurrentUser = userLogin.getUserLoginID() == loggedInUserID;

            String userJSON = "{" +
                    "\"id\": " + userLogin.getUserLoginID() + "," +
                    "\"email\": \"" + userLogin.getEmail() + "\"," +
                    "\"canCreate\": " + userLogin.isCanCreateUsers() + "," +
                    "\"lastLoggedIn\": " + "\"" + lastTimeLoggedInString + "\"," +
                    "\"isCurrentUser\": " + isCurrentUser +
                    "}";
            if (userLoginIterator.hasNext())
                userJSON += ",";
            json.append(userJSON);
        }

        json.append("]");

        printDebug("all users json", json);
        return json.toString();
    }

    /**
     * Get Max Lengths for input fields
     *
     * @return Max Lengths
     */
    @Path("/maxInputLength")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaxLength() {
        JSONParser parser = new JSONParser();

        String out = "{";

        try {
            out += parser.listToJSON(DataTypeInfo.getValues(), DataTypeInfo.class, ParserResponseType.KEY_VALUE_PAIR);
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
            return Response.status(500).entity("{ \"success\": false }").build();
        }

        out += ", \"success\": true }";

        printDebug("max input length", out);
        return Response.status(200).entity(out).build();
    }

    /**
     * @param tempAnsweredQuestion
     * @return
     * @author Tim Irmler
     * @since 20.08.2021
     */
    private String buildSingleAnsweredQuestionJsonResponse(TempAnsweredQuestion tempAnsweredQuestion) {
        return buildSingleAnsweredQuestionJsonResponse(tempAnsweredQuestion, false);
    }

    /**
     * @param tempAnsweredQuestion
     * @param withAnswers
     * @return
     * @author Tim Irmler
     */
    private String buildSingleAnsweredQuestionJsonResponse(TempAnsweredQuestion tempAnsweredQuestion, boolean withAnswers) {
        ArrayList<TempAnsweredQuestion> answeredQuestions = new ArrayList<>();
        answeredQuestions.add(tempAnsweredQuestion);

        return buildAnsweredQuestionJsonResponse(answeredQuestions, withAnswers);
    }

    /**
     * @param tempAnsweredQuestions
     * @return
     * @author Tim Irmler
     * @since 20.08.2021
     */
    private String buildAnsweredQuestionJsonResponse(List<TempAnsweredQuestion> tempAnsweredQuestions) {
        return buildAnsweredQuestionJsonResponse(tempAnsweredQuestions, false);
    }

    /**
     * @param tempAnsweredQuestions
     * @return
     * @author Tim Irmler
     * @since 17.08.2021
     */
    private String buildAnsweredQuestionJsonResponse(List<TempAnsweredQuestion> tempAnsweredQuestions, boolean withAnswers) {
        StringBuilder json = new StringBuilder("[");

        if (tempAnsweredQuestions != null) {
            Iterator<TempAnsweredQuestion> tempAnsweredQuestionIterator = tempAnsweredQuestions.iterator();
            while (tempAnsweredQuestionIterator.hasNext()) {
                if (!withAnswers) {
                    String answeredQuestionString = "{";
                    TempAnsweredQuestionTimesResult answeredQuestionTimesResult = tempAnsweredQuestionIterator.next().getAnsweredQuestionTimesResult();
                    AnsweredQuestion answeredQuestion = answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getAnsweredQuestionStatistik().getAnsweredQuestion();
                    answeredQuestionString += "\"id\":" + answeredQuestion.getAnsweredQuestionID() + ",";
                    answeredQuestionString += "\"question\":\"" + answeredQuestion.getQuestion() + "\",";
                    answeredQuestionString += "\"upvotes\":" + answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getUpvote() + ",";
                    answeredQuestionString += "\"downvotes\":" + answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getDownvote() + ",";
                    answeredQuestionString += "\"views\":" + answeredQuestionTimesResult.getViews();

                    answeredQuestionString += "}";
                    json.append(answeredQuestionString);

                    if (tempAnsweredQuestionIterator.hasNext()) json.append(", ");
                } else {
                    String answeredQuestionString = "{";

                    TempAnsweredQuestion tempAnsweredQuestion = tempAnsweredQuestionIterator.next();
                    AnsweredQuestion answeredQuestion = tempAnsweredQuestion.getAnsweredQuestion();
                    answeredQuestionString += "\"id\":" + answeredQuestion.getAnsweredQuestionID() + ",";
                    answeredQuestionString += "\"question\":\"" + answeredQuestion.getQuestion() + "\",";
                    answeredQuestionString += generateAllAnswersJson(tempAnsweredQuestion.getTempAnswers(), null);

                    answeredQuestionString += "}";
                    json.append(answeredQuestionString);

                    if (tempAnsweredQuestionIterator.hasNext()) json.append(", ");
                }
            }
        }

        json.append("]");

        printDebug("answeredQuestions json", json.toString());

        return json.toString();
    }

    /**
     * @param tempAnsweredQuestionTimesResults
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    private String buildAnsweredQuestionPerAnswerJsonResponse(List<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults) {
        StringBuilder json = new StringBuilder("[");

        if (tempAnsweredQuestionTimesResults != null) {
            Iterator<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResultIterator = tempAnsweredQuestionTimesResults.iterator();
            while (tempAnsweredQuestionTimesResultIterator.hasNext()) {
                String answeredQuestionString = "{";
                TempAnsweredQuestionTimesResult answeredQuestionTimesResult = tempAnsweredQuestionTimesResultIterator.next();
                AnsweredQuestion answeredQuestion = answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getAnsweredQuestionStatistik().getAnsweredQuestion();
                answeredQuestionString += "\"id\":" + answeredQuestion.getAnsweredQuestionID() + ",";
                answeredQuestionString += "\"question\":\"" + answeredQuestion.getQuestion() + "\",";
                answeredQuestionString += "\"upvotes\":" + answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getUpvote() + ",";
                answeredQuestionString += "\"downvotes\":" + answeredQuestionTimesResult.getAnsweredQuestionTimesResult().getDownvote() + ",";
                answeredQuestionString += "\"views\":" + answeredQuestionTimesResult.getViews();

                answeredQuestionString += "}";
                json.append(answeredQuestionString);

                if (tempAnsweredQuestionTimesResultIterator.hasNext()) json.append(", ");
            }
        }

        json.append("]");

        printDebug("answeredQuestions per answer json", json.toString());

        return json.toString();
    }

    /**
     * @param unansweredQuestions
     * @return
     * @author Tim Irmler
     * @since 19.08.2021
     */
    private String buildUnansweredQuestionJsonResponse(List<TempUnansweredQuestion> unansweredQuestions) {
        StringBuilder json = new StringBuilder("[");

        if (unansweredQuestions != null) {
            Iterator<TempUnansweredQuestion> tempUnansweredQuestionIterator = unansweredQuestions.iterator();
            while (tempUnansweredQuestionIterator.hasNext()) {
                String unansweredQuestionString = "{";
                TempUnansweredQuestion tempUnansweredQuestion = tempUnansweredQuestionIterator.next();
                UnansweredQuestion unansweredQuestion = tempUnansweredQuestion.getUnansweredQuestion();
                unansweredQuestionString += "\"id\":" + unansweredQuestion.getUnansweredQuestionID() + ",";
                unansweredQuestionString += "\"question\":\"" + unansweredQuestion.getQuestion() + "\",";
                unansweredQuestionString += "\"views\":" + tempUnansweredQuestion.getViews();

                unansweredQuestionString += "}";
                json.append(unansweredQuestionString);

                if (tempUnansweredQuestionIterator.hasNext()) json.append(", ");
            }
        }

        json.append("]");

        printDebug("unanswered question json", json.toString());

        return json.toString();
    }

    @GET
    @Path("/checkNewBlacklist")
    public Response checkCanMatchBlacklist(@QueryParam("word") String word) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        TagDAO tagDAO = new TagDAO();
        Tag tag = tagDAO.selectByTag(word, em);

        em.getTransaction().commit();
        em.close();

        boolean isTag = tag != null;

        return Response.status(200).entity("{\"isTag\": " + isTag + "}").build();
    }
}
