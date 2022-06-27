package com.ubs.backend.services;

import com.ubs.backend.classes.BotResult;
import com.ubs.backend.classes.Evaluation;
import com.ubs.backend.classes.IntentResponse;
import com.ubs.backend.classes.Levenshtein;
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
 * @author Marc
 * @author Magnus
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Path("find")
public class IntentFinder {
    /**
     * define the min levenshtein distance, if the distance is more than this min, ignore the word.
     *
     * @since 17.07.2021
     */
    private static final float MINLEVENSHTEINDISTANCE = 0.6f;
    private static final float MAXLEVENSHTEINDISTANCE = 0.9f;
    /**
     * define if we should output debug stuff or not
     *
     * @since 17.07.2021
     */
    private static final boolean DEBUG = true;
    /**
     * all the found matches
     *
     * @since 17.07.2021
     */
    ArrayList<Match> matchesFound = new ArrayList<>();

    /**
     * Restful Service to find an answer to the input String.
     * This function will:
     * Convert the input string to an array of words
     * Read all possible answers from the database
     * Check if the word pair match is blacklisted
     * Search for the best answer possible
     * Return a json object with all important data
     *
     * @param input the Input String for which the service will search the correct answer for
     * @return a json object with all found tags, bot certainties and original words and the answer
     * @since 17.07.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/answer")
    public Response findAnswer(@QueryParam("q") String input) {
        IntentResponse response = search(input, true, 0);
        return Response.status(200).entity(generateJSONResponse(response)).build();
    }

    /**
     * @param input
     * @return
     * @author Marc
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public IntentResponse search(String input, boolean affectStatistics, int answerIndex) {
        ResultDAO resultDAO = new ResultDAO();
        TypeTagDAO typeTagDAO = new TypeTagDAO();
        BlacklistEntryDAO blacklistEntryDAO = new BlacklistEntryDAO();
        MatchDAO matchDAO = new MatchDAO();
        AnswerDAO answerDAO = new AnswerDAO();

        printDebug("Input (raw)", input);

        input = prepareString(input, 255, true, false);
        input = input.trim().replaceAll(" +", " "); // replace all spaces that have more than one space ("hello    world      " -> "hello world")
        printDebug("Input (unfiltered, trimmed, escaped, prepared)", input);

        String[] words = input.split("\\s"); // All words in the input string

        printDebug("Found Words (unfiltered, with duplicates)", Arrays.toString(words));

        words = new HashSet<>(Arrays.asList(words)).toArray(new String[0]);

        printDebug("Found Words (unfiltered, no duplicates)", Arrays.toString(words));

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<Result> dbResults = resultDAO.select(em); // List of all Results in the Database
        List<TypeTag> dbTypeTags = typeTagDAO.select(em); // list of all tags that are in a grouped type (example: joke)
        List<Match> matches = matchDAO.select(em); // List of all Matches in the database
        ArrayList<String> filteredWords = new ArrayList<>();

        String inputFiltered = input;
        for (String word : words) {
            if (isBlacklisted(word, blacklistEntryDAO, em)) {
                BlacklistEntry blacklistEntryFromDB = blacklistEntryDAO.selectByWord(word, em);
                inputFiltered = inputFiltered.replaceAll(blacklistEntryFromDB.getWord(), "[removed]");
                input = input.replaceAll(blacklistEntryFromDB.getWord() + " ", ""); // replace the blacklist entry and its following space1
                input = input.replaceAll(blacklistEntryFromDB.getWord(), ""); // if the blacklist entry doesn't have a space afterwards, replace just the blacklist entry
            } else {
                filteredWords.add(word);
            }

        }
        printDebug("Input (filtered, not saved in DB)", inputFiltered);
        printDebug("Input (filtered, saved in DB)", input);
        printDebug("Found words (filtered)", filteredWords.toString());

        ArrayList<BotResult> possibleResults = new ArrayList<>(); // List where all possible results will be added to

        ArrayList<Evaluation> evaluations = new ArrayList<>();

        // Loop through all Results
        printDebug("Now Checking For Tags in the Text", "------------------------", 3);
        if (filteredWords.size() > 0) {
            for (Result r : dbResults) {
                printDebug("Changing Result", "Current Result -> " + r.getTag().getTag(), 1);
                // Loop through all words
                for (String w : filteredWords) {
                    if (isBadMatch(w, r.getTag(), matches)) continue; // if translation is denied, continue

                    int dif = Math.max(r.getTag().getTag().length(), w.length()); // get length of shorter word (tag / word in the input)
                    double dist = 1 - (double) Levenshtein.calculate(r.getTag().getTag(), w, false) / (dif); // calculate Levenshtein distance 1 > 0
                    printDebug("Levenshtein Distance", "(" + w + ") -> (" + r.getTag().getTag() + ") -> " + dist);
                    // Check if Levenshtein distance is good enough to count as a valid answer
                    if (dist > MINLEVENSHTEINDISTANCE) {
                        Evaluation eval = getEvaluation(dist, r, r.getAnswer(), w, em);
                        evaluations.add(eval);
                        possibleResults.add(new BotResult(r, r.getAnswer(), w, dist));
                    }
                }
            }

            HashMap<AnswerType, Answer> randomAnswerPerType = new HashMap<>();
            for (AnswerType answerType : AnswerType.getValues()) {
                if (answerType.isGroupedTags()) {
                    List<Answer> randomAnswer = answerDAO.selectRandomByType(answerType, 1, em);
                    if (randomAnswer != null) {
                        randomAnswerPerType.put(answerType, randomAnswer.get(0));
                    }
                }
            }

            printDebug("randomAnswerPerType", randomAnswerPerType.toString());

            for (TypeTag typeTag : dbTypeTags) {
                Answer currentAnswer = randomAnswerPerType.get(typeTag.getAnswerType());
                if (currentAnswer != null) {
                    printDebug("Changing TypeTag", "Current TypeTag -> " + typeTag.getTag().getTag());
                    printDebug("current answer type", "Current answer type -> " + typeTag.getAnswerType());
                    printDebug("Current Answer", "Current Answer -> " + currentAnswer);
                    // Loop through all words
                    for (String w : filteredWords) {
                        if (isBadMatch(w, typeTag.getTag(), matches)) continue; // if translation is denied, continue

                        int dif = Math.max(typeTag.getTag().getTag().length(), w.length()); // get length of shorter word (tag / word in the input)
                        double dist = 1 - (double) Levenshtein.calculate(typeTag.getTag().getTag(), w, false) / (dif); // calculate Levenshtein distance 1 > 0
                        printDebug("Levenshtein Distance", "(" + w + ") -> (" + typeTag.getTag().getTag() + ") -> " + dist);
                        // Check if Levenshtein distance is good enough to count as a valid answer
                        if (dist > MINLEVENSHTEINDISTANCE) {
                            Evaluation eval = getEvaluation(dist, typeTag, currentAnswer, w, em);
                            evaluations.add(eval);
                            possibleResults.add(new BotResult(typeTag, currentAnswer, w, dist));
                        }
                    }
                } else {

                }
            }
        } else {
            printDebug("No words found!", "No words have been found! they probably all got filtered out!");
        }

        // Sort with Levenshtein distance
        ArrayList<Double> certainties = new ArrayList<>();
        for (BotResult b : possibleResults) {
            certainties.add(b.getCertainty());
        }
        Collections.sort(certainties);
        Collections.reverse(certainties);
        printDebug("certainties", certainties.toString());
        ArrayList<BotResult> cacheBotResult = new ArrayList<>();
        List<BotResult> matchedResults = new ArrayList<>();
        for (Double certain : certainties) {
            for (BotResult b : possibleResults) {
                if (b.getCertainty() == certain) {
                    if (matchedResults.contains(b)) continue;
                    matchedResults.add(b);
                    cacheBotResult.add(b);
                    break;
                }
            }
        }
        for (BotResult b : possibleResults)
            printDebug("Old Sorted", "\n" + b);
        for (BotResult b : cacheBotResult)
            printDebug("new Sorted", "\n" + b);
        possibleResults = cacheBotResult;

        HashMap<Answer, Double> answerMap = new HashMap<>();
        printDebug("Adding the Ratings from the Answers together", "------------------------", 3);
        // Add together all ratings from the same answers
        for (Evaluation e : evaluations) {
            double value = (answerMap.get(e.getAnswer()) == null) ? 0 : answerMap.get(e.getAnswer());
            value += e.getRating();
            answerMap.put(e.getAnswer(), value);
            printDebug("Add Rating to Answer", "Answer(" + e.getAnswer() + ") -> " + value + " | added " + e.getRating());
        }

        // If no answer was found, return with a default answer
        printDebug("Checking If an Answer Was Found", "------------------------", 3);
        if (answerMap.size() == 0) {
            if (!input.equals("") && affectStatistics) {
                addUnansweredQuestionToDB(input, em);
            }

            em.getTransaction().commit();
            em.close();
            printDebug("Return", "No Answer Found");
            return new IntentResponse("404", new ArrayList<>(), new ArrayList<>(), -1);
        }

        // find the answer with the best rating
//        Map.Entry<Answer, Double> bestAnswer = answerMap.entrySet().iterator().next();
        List<Evaluation> sortedAnswers = new ArrayList<>();

        for (Map.Entry<Answer, Double> entry : answerMap.entrySet()) {
            sortedAnswers.add(new Evaluation(entry.getKey(), null, entry.getValue()));
        }

        for (int i = 0; i < sortedAnswers.size(); i++) {
            for (int j = 0; j < sortedAnswers.size(); j++) {
                if (j + 1 >= sortedAnswers.size()) continue;
                Evaluation first = sortedAnswers.get(j);
                Evaluation second = sortedAnswers.get(j + 1);

                if (second.getRating() > first.getRating()) {
                    sortedAnswers.set(j, second);
                    sortedAnswers.set(j + 1, first);
                }
            }
        }
        Evaluation bestAnswer = (answerIndex >= sortedAnswers.size()) ? sortedAnswers.get(sortedAnswers.size() - 1) : sortedAnswers.get(answerIndex);

        printDebug("Sorted Answers", sortedAnswers);
//
//        for (Map.Entry<Answer, Double> entry : answerMap.entrySet()) {
//            printDebug("Answer Found", "Answer (" + entry.getKey().getTitle() + ") -> " + entry.getValue());
//            if (entry.getValue() > bestAnswer.getValue()) bestAnswer = entry;
//        }

        printDebug("The Best answer was found", "------------------------", 3);
        printDebug("Best Answer", bestAnswer.getAnswer().getTitle());

        // Store all Results where the answer equals the best answer in an Arraylist
        ArrayList<BotResult> finalResults = new ArrayList<>();
        boolean viewed = false;
        for (BotResult e : possibleResults) {
            if (e.getAnswer().getTitle().equals(bestAnswer.getAnswer().getTitle())) {
                boolean foundResult = false;
                for (BotResult r : finalResults) {
                    if (r.getResult().equals(e.getResult())) {
                        foundResult = true;
                        break;
                    }
                }

                if (!foundResult) {
                    printDebug("Increasing view result", "increasing view of result " + e.getResult());
                    resultDAO.view(e.getResult());
                }
                finalResults.add(e);
                if (!viewed) {
                    printDebug("Increasing view Answer", "increasing view of answer " + e.getAnswer());
                    answerDAO.view(e.getAnswer());

                    viewed = true;
                }
            }
        }

        // Add answered Question to the DB
        if (!input.equals("") && affectStatistics) {
            addAnsweredQuestionToDB(input, finalResults, em);
        }

        List<UploadFile> responseFiles = null;

        for (Answer a : answerDAO.select()) {
            if (a.getTitle().equals(bestAnswer.getAnswer().getTitle())) {
                List<UploadFile> ufiles = a.getFiles();

                if (ufiles == null) break;

                responseFiles = ufiles;
            }
        }

        IntentResponse out = new IntentResponse(bestAnswer.getAnswer().getAnswer(), finalResults, responseFiles, bestAnswer.getAnswer().getAnswerID());
        em.getTransaction().commit();
        em.close();


        return out;
    }

    /**
     * Add questions that cannot be answered to the db
     *
     * @param question the question itself
     * @param em       the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private void addUnansweredQuestionToDB(String question, EntityManager em) {


        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        answeredQuestionDAO.removeByQuestion(question, em);

        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        StatistikTimes statistikTimes = statistikTimesDAO.selectNow(true, em);


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
     * @param question                   the question itself, how it should be displayed
     * @param answeredQuestionBotResults all botresults with their tags that are used in the question and the answer the question gets
     * @param em                         the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private void addAnsweredQuestionToDB(String question, List<BotResult> answeredQuestionBotResults, EntityManager em) {
        UnansweredQuestionDAO unansweredQuestionDAO = new UnansweredQuestionDAO();
        unansweredQuestionDAO.removeByQuestion(question, em);

        List<Result> answeredQuestionResults = new ArrayList<>();
        List<TypeTag> answeredQuestionTypeTags = new ArrayList<>();
        for (BotResult botResult : answeredQuestionBotResults) {
            if (botResult.getAnswer().getAnswerType().isGroupedTags()) {
                answeredQuestionTypeTags.add((TypeTag) botResult.getResult());
            } else {
                answeredQuestionResults.add((Result) botResult.getResult());
            }
        }

        if (answeredQuestionResults.size() > 0 || answeredQuestionBotResults.size() > 0) {

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
                        answeredQuestion = new AnsweredQuestion(question, answeredQuestionResults.get(0).getAnswer().isHidden());

                        answeredQuestionDAO.insert(answeredQuestion, em);

                        
                        for (Result result : answeredQuestionResults) {
                            AnsweredQuestionResult answeredQuestionResult = new AnsweredQuestionResult(answeredQuestion, result);
                            answeredQuestionTimesResults.add(new AnsweredQuestionTimesResult(null, answeredQuestionResult));

                            em.persist(answeredQuestionResult);
                        }
                    } else if (answeredQuestionTypeTags.size() > 0) {
                        answeredQuestion = new AnsweredQuestion(question, answeredQuestionTypeTags.get(0).getAnswerType().isHidden());

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

    /**
     * Method to check if a word is blacklisted.
     * Goes through array and checks if it is contained.
     *
     * @param word              the word we are looking for
     * @param blacklistEntryDAO the blacklistentry dao
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
     * Method to check if a "Translation/Match" is bad.
     * It's considered bad if upvotes / downvotes is under 0.666 and has 3 downvotes more then upvotes.
     *
     * @param word    the matched word.
     * @param tag     the tag with which the word was matched
     * @param matches the list of all matches
     * @return if the match is bad
     * @author Magnus
     * @author Marc
     * @since 17.07.2021
     */
    private boolean isBadMatch(String word, Tag tag, List<Match> matches) {
        for (Match t : matches) {
            if (!t.getWord().equalsIgnoreCase(word)) continue;
            if (!t.getTag().equals(tag)) continue;
            int upvotes = t.getUpvote();
            int downvotes = t.getDownvote();
            float value = upvotes / (float) downvotes;
            printDebug("Match Found", "(Word -> " + t.getWord() + ") (Tag -> " + t.getTag().getTag() + ") (Value -> " + value + ")");
            matchesFound.add(t);
            if (value < 2f / 3f && downvotes - upvotes > 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to generate the JSON Response for the Request
     *
     * @param response Object containing all important information for the response
     * @return the JSON Object stringified
     * @author Marc
     * @since 17.07.2021
     */
    public String generateJSONResponse(IntentResponse response) {
        StringBuilder json = new StringBuilder("{ \"answer\":{\"answer\":\"" + response.getAnswer() + "\", \"id\":" + response.getId() + " }, \"matches\":[");

        Iterator<BotResult> resultIterator = response.getResults().iterator();
        while (resultIterator.hasNext()) {
            BotResult e = resultIterator.next();
            printDebug("BotResult", "Tag: " + e.getResult().getTag().getTag(), 3);
            if (matchesFound != null)
                for (Match m : matchesFound) {
                    if (e.getWord().equals(m.getWord()) && e.getResult().getTag().getTag().equals(m.getTag().getTag())) {
                        float certainty = 0.5f;
                        if (m.getUpvote() != 0) {
                            float together = m.getUpvote() + m.getDownvote();
                            certainty = m.getUpvote() / together;
                        }
                        printDebug("Match or Result", "(" + m.getWord() + ") -> (" + m.getTag().getTag() + ") up: " + m.getUpvote() + " down: " + m.getDownvote() + " Certainty: " + certainty);
                        if (certainty > MAXLEVENSHTEINDISTANCE) {
                            printDebug("Change Certainty", "Certainty: (" + e.getCertainty() + ") -> (5)");
                            e.setCertainty(5);
                        }
                        break;
                    }
                }
            else
                printDebug("No Match", "there was no match");
            String match = "{ \"tag\":" + e.getResult().getTag().getTagID() + ", \"word\":" + '"' + e.getWord() + '"' + ", \"certainty\":" + e.getCertainty() + "}";
            json.append(match);

            if (resultIterator.hasNext())
                json.append(",");
        }

        json.append("]").append(", \"files\": [");

        if (response.getFiles() != null) {
            Iterator<UploadFile> fileIterator = response.getFiles().iterator();
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

        printDebug("JSON Generated", json.toString());

        return json.toString();
    }

    /**
     * @param dist
     * @param resultParent
     * @param answer
     * @param w
     * @param em
     * @return
     * @author Tim Irmler
     * @since 21.07.2021
     */
    private Evaluation getEvaluation(double dist, ResultParent resultParent, Answer answer, String w, EntityManager em) {
        Tag tag = resultParent.getTag();
        int upvotes = resultParent.getUpvotes();
        int downvotes = resultParent.getDownvotes();
        MatchDAO matchDAO = new MatchDAO();
        if (dist < MAXLEVENSHTEINDISTANCE) { // if bot is not that sure add the pair (Tag and Word) to the database
            Match trans = new Match(tag, w);
            printDebug("Match found", "Word(" + w + ") -> Tag(" + tag.getTag() + ") Dist(" + dist + ")");
            long count = 0;
            matchDAO.autoInsert(trans, em);
            if (count == 0) {
                printDebug("Match insert", "Match(" + tag.getTag() + ", " + w + ")");
                matchesFound.add(trans);
            }
        }

        // Calculate the Up/Down vote rating for the answer
        double rating = (upvotes == downvotes) ? 0.5 : (double) ((100f / (upvotes + downvotes)) * upvotes) / 100; // 1 > 0
        printDebug("Rating", "up: " + upvotes + ", down: " + downvotes + ", rating: " + rating);

        Evaluation eval = new Evaluation(answer, tag.getTag(), rating);

        printDebug("Path to Answer", "Word(" + w + ") -> Tag(" + tag.getTag() + ") -> Answer(" + answer.getAnswer() + ") -> Rating " + rating);

        return eval;
    }
}
