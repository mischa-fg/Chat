package com.ubs.backend.services;

import com.ubs.backend.classes.PossibleAnswer;
import com.ubs.backend.classes.WordLevenshteinDistance;
import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.*;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.UnansweredQuestionDAO;
import com.ubs.backend.classes.database.dao.statistik.AnsweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.dao.statistik.UnansweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.AnsweredQuestionResult;
import com.ubs.backend.classes.database.questions.AnsweredQuestionTimesResult;
import com.ubs.backend.classes.database.questions.UnansweredQuestion;
import com.ubs.backend.classes.database.statistik.AnsweredQuestionStatistik;
import com.ubs.backend.classes.database.statistik.UnansweredQuestionStatistik;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.enums.DataTypeInfo;
import com.ubs.backend.util.CalculateRating;
import com.ubs.backend.util.Levenshtein;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.ubs.backend.util.PrepareString.prepareString;
import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * @author Tim Irmler
 * @since 10.08.2021
 */
@Path("find2")
public class IntentFinderNew {
    /**
     * if the levenshtein distance is equals to this or higher, we are certain that the user meant this word!
     */
    private static final float levenshteinCertain = 0.9f;

    /**
     * if the levenshtein distance is equals or higher to this, we are not certain, but it could be that the user meant this word so the word is classified as match
     * anything below this number is ignored
     */
    private static final float levenshteinMatch = 0.6f;

    /**
     * WebService for getting an Answer to an Input question
     *
     * @param input the question from the Frontend
     * @return HTTP Response which contains the Answer which will be displayed on the Frontend. (Can Contain Files)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/answer2")
    public Response findAnswer(@QueryParam("q") String input) {
        return Response.status(200).entity(search(input, true)).build();
    }

    /**
     * Takes a String as an Input and compares it with the Tags from the Database
     * Matches Tags to Words in the String
     *
     * @param input            the input Question
     * @param affectStatistics if the statistics in the Database will be updated
     * @return the Best Answer as a JSON String
     */
    public String search(String input, boolean affectStatistics) {
        ResultDAO resultDAO = new ResultDAO();
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        BlacklistEntryDAO blacklistEntryDAO = new BlacklistEntryDAO();
        TagDAO tagDAO = new TagDAO();

        printDebug("Input (raw)", input);

        input = prepareString(input, DataTypeInfo.USER_QUESTION_INPUT.getMaxLength(), true, false, false);
        printDebug("Input (unfiltered, trimmed, escaped, prepared)", input);

        String[] words = input.split("\\s"); // All words in the input string

        printDebug("Found Words (unfiltered, with duplicates)", Arrays.toString(words));

        words = new HashSet<>(Arrays.asList(words)).toArray(new String[0]); // Remove any duplicates

        printDebug("Found Words (unfiltered, no duplicates)", Arrays.toString(words));

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        ArrayList<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (isBlacklisted(word, blacklistEntryDAO, em)) {
                BlacklistEntry blacklistEntryFromDB = blacklistEntryDAO.selectByWord(word, em);
                input = input.replaceAll(blacklistEntryFromDB.getWord() + " ", ""); // replace the blacklist entry and its following space
                input = input.replaceAll(blacklistEntryFromDB.getWord(), ""); // if the blacklist entry doesn't have a space afterwards, replace just the blacklist entry
            } else {
                filteredWords.add(word.replace("?", ""));
            }
        }
        printDebug("Input (filtered, saved in DB)", input);
        printDebug("Found words (filtered)", filteredWords.toString(), 0, 2);

        List<Tag> tags = tagDAO.select(em);
        ArrayList<AnswerType> foundAnswerTypes = new ArrayList<>();
        ArrayList<WordLevenshteinDistance> wordLevenshteinDistances = new ArrayList<>();
        printDebug("checking distance of each word and tag", "", 0, 1);
        for (String word : filteredWords) {
            printDebug("changing word", "word = " + word);
            for (Tag tag : tags) {
                float dist = CalculateRating.getLevenshteinDistance(Levenshtein.calculate(word, tag.getTag(), false), Math.max(tag.getTag().length(), word.length()));

                if (dist >= levenshteinMatch) {
                    printDebug("distance: " + word + " -> " + tag.getTag(), dist);
                    List<Result> results = resultDAO.selectByTag(tag.getTagID(), em);
                    printDebug("results found of tag '" + tag.getTag() + "'", results.size());
                    for (Result result : results) {
                        wordLevenshteinDistances.add(new WordLevenshteinDistance(word, result, dist, levenshteinCertain, em));
                    }

                    List<TypeTag> typeTags = typeTagDAO.selectByTag(tag.getTagID(), em);
                    printDebug("typeTags found of tag '" + tag.getTag() + "'", typeTags.size());
                    for (TypeTag typeTag : typeTags) {
                        wordLevenshteinDistances.add(new WordLevenshteinDistance(word, typeTag, dist, levenshteinCertain, em));
                        if (!foundAnswerTypes.contains(typeTag.getAnswerType())) {
                            foundAnswerTypes.add(typeTag.getAnswerType());
                        }
                    }

                    printDebug("end tag", "", 0, 1);
                }
            }
            printDebug("end word", "", 0, 2);
        }

        printDebug("total wordLevenshteinDistances", wordLevenshteinDistances.size(), 0, 2);

        printDebug("putting answers of grouped answertypes into hashmap", "", 0, 2);
        HashMap<AnswerType, Answer> answerTypeAnswerHashMap = new HashMap<>();
        for (AnswerType answerType : foundAnswerTypes) {
            answerTypeAnswerHashMap.put(answerType, answerType.handle(null));
        }

        ArrayList<PossibleAnswer> possibleAnswers = new ArrayList<>();
        printDebug("going through all found wordLevenshteinDistances", "adding/merging possible answers");
        for (WordLevenshteinDistance wordLevenshteinDistance : wordLevenshteinDistances) {
            PossibleAnswer possibleAnswer = null;
            Answer answer = null;
            printDebug("current wordLevenshteinDistance", wordLevenshteinDistance, 1);
            int[] upDownvotes = wordLevenshteinDistance.getUpDownvotes();
            if (wordLevenshteinDistance.getResultParent() instanceof Result) {
                if (upDownvotes != null) {
                    Result result = (Result) wordLevenshteinDistance.getResultParent();
                    answer = result.getAnswer();
                    possibleAnswer = new PossibleAnswer(answer, upDownvotes[0], upDownvotes[1], wordLevenshteinDistance);
                } else {
                    printDebug("UPDOWNVOTES = ", "null");
                }
            } else {
                if (upDownvotes != null) {
                    TypeTag typeTag = (TypeTag) wordLevenshteinDistance.getResultParent();
                    answer = answerTypeAnswerHashMap.get(typeTag.getAnswerType());
                    possibleAnswer = new PossibleAnswer(answer, upDownvotes[0], upDownvotes[1], wordLevenshteinDistance);
                } else {
                    printDebug("UPDOWNVOTES = ", "null");
                }
            }
            if (possibleAnswer != null) {
                PossibleAnswer possibleAnswerFromArray = getPossibleAnswerFromArray(possibleAnswers, answer);
                if (possibleAnswerFromArray == null) {
                    possibleAnswers.add(possibleAnswer);
                    printDebug("possible answer = " + possibleAnswer.getAnswer().getTitle(), "possible answer not already existing! adding to array...");
                } else {
                    possibleAnswerFromArray.increaseUpvotes(possibleAnswer.getUpvotes());
                    possibleAnswerFromArray.increaseDownvotes(possibleAnswer.getDownvotes());
                    possibleAnswerFromArray.addFoundWordLevenshteinDistance(possibleAnswer.getFoundWordLevenshteinDistances());
                    printDebug("possible answer = " + possibleAnswer.getAnswer().getTitle(), "possible answer already existing! merging values...");
                }
            } else {
                printDebug("POSSIBLE ANSWER IS NULL", "because return of upDownvotes is null because bad match");
            }
        }

        printDebug("total possibleAnswers", possibleAnswers.size(), 2);
        printDebug("possible answers", possibleAnswers.toString(), 0, 2);
        for (PossibleAnswer possibleAnswer : possibleAnswers) {
            possibleAnswer.setRating();
            printDebug("rating of possible answer '" + possibleAnswer.getAnswer().getTitle() + "'", possibleAnswer.getRating(), 0, 1);
        }

        float maxRating = -1f;
        PossibleAnswer bestAnswer = null;
        for (PossibleAnswer possibleAnswer : possibleAnswers) {
            if (possibleAnswer.getRating() > maxRating) {
                maxRating = possibleAnswer.getRating();
                bestAnswer = possibleAnswer;
            }
        }

        if (bestAnswer == null) {
            if (!input.equals("") && affectStatistics) {
                addUnansweredQuestionToDB(input, em);
            }

            em.getTransaction().commit();
            em.close();
            printDebug("Return", "No Answer Found");
            return generateJSONResponse(new PossibleAnswer(new Answer("404", AnswerType.ERROR), new WordLevenshteinDistance("Error", null, 0, -1, null)));
        }

        printDebug("max rating", maxRating, 1);
        printDebug("best answer", bestAnswer.getAnswer());

        if (bestAnswer.getAnswer().isHidden()) {
            affectStatistics = false;
        }
        if (affectStatistics) {
            printDebug("increasing view of best answer", "", 1);
            if (bestAnswer.getAnswer().getAnswerID() > 0) {
                AnswerDAO answerDAO = new AnswerDAO();
                answerDAO.view(bestAnswer.getAnswer(), em);
            }
        }

        for (WordLevenshteinDistance wordLevenshteinDistance : bestAnswer.getFoundWordLevenshteinDistances()) {
            printDebug("current wordLevenshteinDistance", wordLevenshteinDistance);
            ResultParent resultParent = wordLevenshteinDistance.getResultParent();
            resultDAO.view(resultParent);
        }

        // Add answered Question to the DB
        if (!input.equals("") && affectStatistics) {
            printDebug("ADDING ANSWERED QUESTION TO DB", "input = " + input);
            addAnsweredQuestionToDB(input, bestAnswer, em);
        } else {
            printDebug("NOT ADDING ANSWERED QUESTION TO DB", "no");
        }

        em.getTransaction().commit();
        em.close();

        return generateJSONResponse(bestAnswer);
    }

    /**
     * Method to check if a word is blacklisted.
     * Goes through array and checks if it is contained.
     *
     * @param word              the word we are looking for
     * @param blacklistEntryDAO the blacklistentry dao
     * @param em                the entity manager
     * @return if the word is blacklisted
     * @author Marc
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private boolean isBlacklisted(String word, BlacklistEntryDAO blacklistEntryDAO, EntityManager em) {
        BlacklistEntry blacklistEntryFromDB = blacklistEntryDAO.selectByWord(word, em);
        if (blacklistEntryFromDB == null) {
            return false;
        } else {
            printDebug("Blacklist", word + " was ignored");
            blacklistEntryFromDB.setUsages(blacklistEntryFromDB.getUsages() + 1);
            blacklistEntryDAO.merge(blacklistEntryFromDB, em);
            return true;
        }
    }

    /**
     * @param possibleAnswers
     * @param answer
     * @return
     * @author Tim Irmler
     * @since 10.08.2021
     */
    private PossibleAnswer getPossibleAnswerFromArray(ArrayList<PossibleAnswer> possibleAnswers, Answer answer) {
        for (PossibleAnswer possibleAnswer : possibleAnswers) {
            if (possibleAnswer.getAnswer().equals(answer)) {
                return possibleAnswer;
            }
        }
        return null;
    }

    /**
     * @param answer
     * @return
     * @author Tim Irmler
     * @since 12.08.2021
     */
    public String generateJSONResponse(PossibleAnswer answer) {
        printDebug("generateJSONResonse()", "generating response of best answer!");

        StringBuilder json = new StringBuilder("{ \"answer\":{\"answer\":\"" + answer.getAnswer().getAnswer() + "\", \"id\":" + answer.getAnswer().getAnswerID() + " }, \"matches\":[");

        printDebug("going through all found wordLevenshteinDistances", "adding found matches");
        boolean addedMatch = false;
        for (WordLevenshteinDistance wordLevenshteinDistance : answer.getFoundWordLevenshteinDistances()) {
            printDebug("current wordLevenshteinDistance", wordLevenshteinDistance, 1);
            if (wordLevenshteinDistance.getMatch() != null) {
                String match = "{ \"id\":" + wordLevenshteinDistance.getMatch().getMatchID() + "}";
                json.append(match).append(",");
                addedMatch = true;
            }
        }
        // removing the last comma
        if (addedMatch) {
            json = new StringBuilder(json.substring(0, json.toString().length() - 1));
        }

        json.append("]").append(", \"foundTags\": [");

        printDebug("going through all found wordLevenshteinDistances", "adding all certain found tags");
        boolean addedTag = false;
        for (WordLevenshteinDistance wordLevenshteinDistance : answer.getFoundWordLevenshteinDistances()) {
            printDebug("current wordLevenshteinDistance", wordLevenshteinDistance, 1);
            if (wordLevenshteinDistance.getResultParent() != null) {
                if (wordLevenshteinDistance.getMatch() == null) {
                    String tag = "{\"id\":" + wordLevenshteinDistance.getResultParent().getTag().getTagID() + "}";
                    json.append(tag).append(",");
                    addedTag = true;
                }
            }
        }
        // removing the last comma
        if (addedTag) {
            json = new StringBuilder(json.substring(0, json.toString().length() - 1));
        }

        json.append("]").append(", \"files\": [");

        printDebug("getting all the files", "", 2);
        if (answer.getAnswer().getFiles() != null) {
            Iterator<UploadFile> fileIterator = answer.getAnswer().getFiles().iterator();
            while (fileIterator.hasNext()) {
                UploadFile f = fileIterator.next();
                json.append("{ \"id\":");
                json.append(f.getFileID());
                json.append(", \"mimeType\":\"");
                json.append(f.getFileType());
                json.append("\", \"fileName\":\"");
                json.append(f.getFileName());
                json.append("\" }");

                if (fileIterator.hasNext()) json.append(", ");
            }
        }

        json.append("] }");

        printDebug("JSON Generated", json.toString(), 2);

        return json.toString();
    }

    /**
     * @param question
     * @param em
     * @author Tim Irmler
     * @since 02.09.2021
     */
    private void addUnansweredQuestionToDB(String question, EntityManager em) {
        addUnansweredQuestionToDB(null, question, em);
    }

    /**
     * Add questions that cannot be answered to the db
     *
     * @param question the question itself
     * @param em       the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private void addUnansweredQuestionToDB(StatistikTimes times, String question, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        answeredQuestionDAO.removeByQuestion(question, em);

        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        StatistikTimes statistikTimes = times;
        if (statistikTimes == null) {
            statistikTimes = statistikTimesDAO.selectNow(true, em);
        }

        UnansweredQuestionStatistikDAO unansweredQuestionStatistikDAO = new UnansweredQuestionStatistikDAO();
        UnansweredQuestionStatistik unansweredQuestionStatistik = unansweredQuestionStatistikDAO.selectByQuestion(question, em);
        UnansweredQuestionDAO unansweredQuestionDAO = new UnansweredQuestionDAO();
        if (unansweredQuestionStatistik == null || unansweredQuestionStatistik.getStatistikTimes().getStatistikID() != statistikTimes.getStatistikID()) {
            UnansweredQuestion unansweredQuestion = unansweredQuestionDAO.selectByQuestion(question, em);
            if (unansweredQuestion == null) {
                unansweredQuestion = new UnansweredQuestion(question);
                unansweredQuestionDAO.insert(unansweredQuestion, em);
            }
            unansweredQuestionStatistik = new UnansweredQuestionStatistik(unansweredQuestion, statistikTimes);
            unansweredQuestionStatistikDAO.insert(unansweredQuestionStatistik, em);
        }

        unansweredQuestionStatistik.increaseAskedAmountDefault();
    }

    /**
     * Add questions that can be answered to the db or update their statistics
     *
     * @param question the question itself, how it should be displayed
     * @param answer   the final answer, containing all the results
     * @param em       the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private void addAnsweredQuestionToDB(String question, PossibleAnswer answer, EntityManager em) {
        UnansweredQuestionDAO unansweredQuestionDAO = new UnansweredQuestionDAO();
        unansweredQuestionDAO.removeByQuestion(question, em);

        ArrayList<Long> idsOfTagsAlreadyUsed = new ArrayList<>();
        List<Result> answeredQuestionResults = new ArrayList<>();
        List<TypeTag> answeredQuestionTypeTags = new ArrayList<>();
        for (WordLevenshteinDistance wordLevenshteinDistance : answer.getFoundWordLevenshteinDistances()) {
            if (!idsOfTagsAlreadyUsed.contains(wordLevenshteinDistance.getResultParent().getTag().getTagID())) {
                if (wordLevenshteinDistance.getResultParent() instanceof Result) {
                    answeredQuestionResults.add((Result) wordLevenshteinDistance.getResultParent());
                } else {
                    answeredQuestionTypeTags.add((TypeTag) wordLevenshteinDistance.getResultParent());
                }
                idsOfTagsAlreadyUsed.add(wordLevenshteinDistance.getResultParent().getTag().getTagID());
            }
        }

        if (answeredQuestionResults.size() > 0 || answeredQuestionTypeTags.size() > 0) {
            boolean isHidden = false;
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            StatistikTimes statistikTimes = statistikTimesDAO.selectNow(true, em);

            AnsweredQuestionStatistikDAO answeredQuestionStatistikDAO = new AnsweredQuestionStatistikDAO();
            AnsweredQuestionStatistik answeredQuestionStatistik = answeredQuestionStatistikDAO.selectByQuestion(question, em);
            AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
            ArrayList<AnsweredQuestionTimesResult> answeredQuestionTimesResults = new ArrayList<>();

            if (answeredQuestionStatistik == null || answeredQuestionStatistik.getStatistikTimes().getStatistikID() != statistikTimes.getStatistikID()) {
                AnsweredQuestion answeredQuestion = answeredQuestionDAO.selectByQuestion(question, em);
                if (answeredQuestion == null) {
                    if (answeredQuestionResults.size() > 0) {
                        isHidden = answer.getAnswer().isHidden();
                        answeredQuestion = new AnsweredQuestion(question, isHidden);
                        answeredQuestionDAO.insert(answeredQuestion, em);

                        for (Result result : answeredQuestionResults) {
                            AnsweredQuestionResult answeredQuestionResult = new AnsweredQuestionResult(answeredQuestion, result);
                            answeredQuestionTimesResults.add(new AnsweredQuestionTimesResult(null, answeredQuestionResult));

                            em.persist(answeredQuestionResult);
                        }
                    } else {
                        isHidden = answer.getAnswer().isHidden();
                        answeredQuestion = new AnsweredQuestion(question, isHidden);
                        answeredQuestionDAO.insert(answeredQuestion, em);

                        for (TypeTag result : answeredQuestionTypeTags) {
                            AnsweredQuestionResult answeredQuestionResult = new AnsweredQuestionResult(answeredQuestion, result);
                            answeredQuestionTimesResults.add(new AnsweredQuestionTimesResult(null, answeredQuestionResult));
                            em.persist(answeredQuestionResult);
                        }
                    }
                }

                answeredQuestionStatistik = new AnsweredQuestionStatistik(answeredQuestion, statistikTimes);
                answeredQuestionStatistikDAO.insert(answeredQuestionStatistik, em);

                if (answeredQuestionTimesResults.size() > 0) {
                    for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                        answeredQuestionTimesResult.setAnsweredQuestionStatistik(answeredQuestionStatistik);
                        em.persist(answeredQuestionTimesResult);
                    }
                } else {
                    List<AnsweredQuestionResult> newAnsweredQuestionResults = answeredQuestionDAO.selectResultByAnsweredQuestionId(answeredQuestionStatistik.getAnsweredQuestion().getAnsweredQuestionID(), em);
                    for (AnsweredQuestionResult answeredQuestionResult : newAnsweredQuestionResults) {
                        em.persist(new AnsweredQuestionTimesResult(answeredQuestionStatistik, answeredQuestionResult));
                    }
                }
            }

            // if the question itself already exists, but it has a new result, add new result with existing question
            AnsweredQuestion answeredQuestion = answeredQuestionDAO.selectByQuestion(question, em);
            for (Result result : answeredQuestionResults) {
                printDebug("checking result by answeredQuestions", result);
                AnsweredQuestionResult alreadyExistingResult = null;
                try {
                    alreadyExistingResult = em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.answeredQuestion.answeredQuestionID = :answeredQuestionID and aqr.result.id = :resultID", AnsweredQuestionResult.class)
                            .setParameter("answeredQuestionID", answeredQuestion.getAnsweredQuestionID()).setParameter("resultID", result.getId()).getSingleResult();
                } catch (NoResultException e) {
                    AnsweredQuestionResult answeredQuestionResult = new AnsweredQuestionResult(answeredQuestion, result);
                    em.persist(answeredQuestionResult);

                    AnsweredQuestionTimesResult answeredQuestionTimesResult = new AnsweredQuestionTimesResult(answeredQuestionStatistik, answeredQuestionResult);
                    em.persist(answeredQuestionTimesResult);
                }
            }

            for (TypeTag result : answeredQuestionTypeTags) {
                AnsweredQuestionResult alreadyExistingResult = null;
                try {
                    alreadyExistingResult = em.createQuery("select aqr from AnsweredQuestionResult aqr where aqr.answeredQuestion.answeredQuestionID = :answeredQuestionID and aqr.typeTag.id = :resultID", AnsweredQuestionResult.class)
                            .setParameter("answeredQuestionID", answeredQuestion.getAnsweredQuestionID()).setParameter("resultID", result.getId()).getSingleResult();
                } catch (NoResultException e) {
                    AnsweredQuestionResult answeredQuestionResult = new AnsweredQuestionResult(answeredQuestion, result);
                    em.persist(answeredQuestionResult);

                    AnsweredQuestionTimesResult answeredQuestionTimesResult = new AnsweredQuestionTimesResult(answeredQuestionStatistik, answeredQuestionResult);
                    em.persist(answeredQuestionTimesResult);
                }

            }

            answeredQuestionStatistik.increaseAskedAmountDefault();
        }
    }
}
