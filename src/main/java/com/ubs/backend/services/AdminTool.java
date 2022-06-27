package com.ubs.backend.services;

import com.ubs.backend.classes.SHA256;
import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.*;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.DefaultQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.UnansweredQuestionDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.DefaultQuestion;
import com.ubs.backend.classes.enums.AnswerType;
import com.ubs.backend.classes.enums.DataTypeInfo;
import com.ubs.backend.util.CalculateRating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ubs.backend.util.PrepareString.prepareString;
import static com.ubs.backend.util.PrepareString.stringTooLong;
import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * Services for the AdminTool
 *
 * @author Marc Andri Fuchs
 * @author Magnus Goetz
 * @author Tim Irmler
 * @author Sarah
 * @since 17.07.2021
 */
@Path("adminTool")
public class AdminTool {
    /**
     * service to add an answer to the DB
     *
     * @param title             the title of the answer
     * @param ans               the answer
     * @param tagsString        all the tags as a simple string, seperated with a comma
     * @param isHidden          if the answer is hidden or not
     * @param answerTypeOrdinal the ordinal value of the answerType
     * @param files
     * @param request           the HTTP request
     * @return status
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Path("/addAnswer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAnswer(@FormParam("aTitle") String title, @FormParam("aText") String ans, @FormParam("tags") String tagsString, @FormParam("isHidden") boolean isHidden, @FormParam("answerTypeOrdinal") int answerTypeOrdinal, @FormParam("files") String files, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        // TODO: create errors for:
        //  - no title
        //  - no answer
        //  - no tags
        //  - (more?)

        // Get Database connection
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();
        ResultDAO resultDAO = new ResultDAO();

        ArrayList<Tag> tags = new ArrayList<>(); // all the tags added to the answer

        // Get all tags
        if (tagsString != null && !tagsString.equals("")) {
            tags = getTagsFromString(tagsString, ",", em);
        }

        AnswerType myAnswerType = AnswerType.getValues()[answerTypeOrdinal];
        if (myAnswerType == null) {
            myAnswerType = AnswerType.DEFAULT;
        }

        Answer answer = new Answer(prepareString(title, DataTypeInfo.ANSWER_TITLE.getMaxLength(), false, false), prepareString(ans, DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false), myAnswerType, isHidden);
        // If the answer shares tags with other answers
        if (answer.getAnswerType().isGroupedTags()) {
            TypeTagDAO typeTagDAO = new TypeTagDAO();
            List<TypeTag> typeTags = typeTagDAO.selectByType(answer.getAnswerType(), em);
            for (Tag tag : tags) {
                addTypeTag(answer.getAnswerType(), tag, typeTags, typeTagDAO, em);
            }
        }
        // If the answer has its own tags
        else {
            // add new answer with tags
            for (Tag tag : tags) {
                resultDAO.insert(new Result(answer, tag), em);
            }
        }

        setAnswerFiles(files, em, answer);

        AnswerDAO answerDAO = new AnswerDAO();
        answerDAO.insert(answer, em);

        em.getTransaction().commit();
        em.close();

        Get get = new Get();
        return get.getSingleAnswer(answer.getAnswerID());
    }

    private void setAnswerFiles(String files, EntityManager em, Answer answer) {
        UploadFileDAO uploadFileDAO = new UploadFileDAO();

        if (files == null || files.equals("")) {
            answer.setFiles(new ArrayList<>());
            return;
        }

        String[] fileStrs = files.split(",");
        long[] fIds = new long[fileStrs.length];
        printDebug("File IDs", Arrays.toString(fileStrs));
        for (int i = 0; i < fileStrs.length; i++) {
            fIds[i] = Long.parseLong(fileStrs[i]);
        }

        List<UploadFile> updatedFiles = new ArrayList<>();
        for (long id : fIds) {
            UploadFile file = uploadFileDAO.select(id, em);
            if (!updatedFiles.contains(file))
                updatedFiles.add(file);
        }
        answer.setFiles(updatedFiles);
    }

    /**
     * service to edit an already existing answer
     *
     * @param answerId          the id of the answer
     * @param title             the title of the answer
     * @param ans               the answer
     * @param tagsString        the new tags to add, as a simple string seperated by a comma
     * @param isHidden          if the answer is hidden or not
     * @param answerTypeOrdinal the ordinal value of the answerType
     * @param files
     * @param request           the HTTP request
     * @return status
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Path("/editAnswer")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response editQuestion(@FormParam("answerId") long answerId, @FormParam("aTitle") String title, @FormParam("aText") String ans, @FormParam("newTags") String tagsString, @FormParam("isHidden") boolean isHidden, @FormParam("answerTypeOrdinal") int answerTypeOrdinal, @FormParam("files") String files, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        // TODO: create errors for:
        //  - no title
        //  - no answer
        //  - (more?)


        AnswerType myAnswerType = AnswerType.getValues()[answerTypeOrdinal];
        if (myAnswerType == null) {
            myAnswerType = AnswerType.DEFAULT;
        }

        // Get Database connection
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        // Get all tags, if tags already exist in db use them. if not, create new tags
        ArrayList<Tag> tags;
        if (tagsString != null && !tagsString.equals("")) {
            tags = getTagsFromString(tagsString, ",", em);
        } else {
            tags = null;
        }

        AnswerDAO answerDAO = new AnswerDAO();
        Answer answerToUpdate = answerDAO.select(answerId, em);
        if (answerToUpdate.getAnswerType().isGroupedTags() != myAnswerType.isGroupedTags()) { // means the answer no longer has its own tags / shares its tags
            if (myAnswerType.isGroupedTags()) { // if we now are grouped, we have to remove all default result tags as the answer now shares the tags with other answers
                ResultDAO resultDAO = new ResultDAO();
                resultDAO.removeByAnswer(answerToUpdate.getAnswerID(), em);
            } else {
                // If the answer now isnt grouped anymore, we dont have to do anything
            }
        }

        // Add or Remove Files from Answer
        setAnswerFiles(files, em, answerToUpdate);

        if (title == null || ans == null) {
            em.getTransaction().rollback();
            return Response.status(400).entity("Parameter is missing!").build();
        }

        answerToUpdate.setTitle(prepareString(title, DataTypeInfo.ANSWER_TITLE.getMaxLength(), true, false));
        answerToUpdate.setAnswer(prepareString(ans, DataTypeInfo.ANSWER_TEXT.getMaxLength(), false, false));
        answerToUpdate.setHidden(isHidden);
        answerToUpdate.setAnswerType(myAnswerType);
        // TODO: answerToUpdate.setFiles();

        if (tags != null) {
            ResultDAO resultDAO = new ResultDAO();
            if (answerToUpdate.getAnswerType().isGroupedTags()) { // if the answer shares tags with others
                TypeTagDAO typeTagDAO = new TypeTagDAO();
                List<TypeTag> typeTags = typeTagDAO.selectByType(answerToUpdate.getAnswerType(), em); // get all type tags with this type
                for (Tag tag : tags) {
                    addTypeTag(answerToUpdate.getAnswerType(), tag, typeTags, typeTagDAO, em); // Add type tag
                }
                resultDAO.removeByAnswer(answerId, em);
            } else { // if the answer has its own tags
                for (Tag tag : tags) {
                    boolean canAddTag = true;
                    if (tag.getTagID() >= 1) { // if the tag already exists in the db, we need to check if we already have a result with this answer and this tag
                        Result resultWithTagAndAnswer = resultDAO.selectByTagAndAnswer(answerId, tag.getTagID(), em);
                        if (resultWithTagAndAnswer != null) {
                            if (tag.getTagID() == resultWithTagAndAnswer.getTag().getTagID()) {
                                canAddTag = false;

                            }
                        }
                    }
                    if (canAddTag) {
                        resultDAO.insert(new Result(answerToUpdate, tag), em);
                    }
                }
            }
        }

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * Webservice to remove a File from an Answer
     *
     * @param answerId the ID of the {@link Answer} which the {@link UploadFile} will be remove from
     * @param fileId the ID of the {@link UploadFile}
     * @param request
     * @return
     */
    @Path("/removeFileFromAnswer") // worked on 21.05.2021 11:38
    @POST
    @Produces("text/plain")
    public Response removeFileFromAnswer(@FormParam("answerId") long answerId, @FormParam("fileId") long fileId, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        UploadFileDAO uploadFileDAO = new UploadFileDAO();
        UploadFile uploadFile = uploadFileDAO.select(fileId, em);
        if (uploadFile != null) {
            AnswerDAO answerDAO = new AnswerDAO();
            Answer answer = answerDAO.select(answerId, em);
            if (answer != null) {
                for (UploadFile file : answer.getFiles()) {
                    if (file.getFileID() == file.getFileID()) {
                        answer.removeFile(file);
                        break;
                    }
                }
            } else {
                em.getTransaction().commit();
                em.close();

                return Response.status(400).entity("Answer not found!").build();
            }
        } else {
            em.getTransaction().commit();
            em.close();

            return Response.status(400).entity("File not found!").build();
        }

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to remove a tag from an answer in the database
     *
     * @param answerId the Answer, where the Tag should be removed
     * @param tagId    the Tag that should be removed
     * @param request  the HttpRequest
     * @return Status 200 if succeeded else 502
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Path("/removeTagFromAnswer") // worked on 21.05.2021 11:38
    @POST
    @Produces("text/plain")
    public Response removeTagFromAnswer(@FormParam("answerId") long answerId, @FormParam("tagId") long tagId, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();
        Answer answer = answerDAO.select(answerId, em);
        if (answer.getAnswerType().isGroupedTags()) {
            TypeTagDAO typeTagDAO = new TypeTagDAO();
            typeTagDAO.removeByTagIdAndType(tagId, answer.getAnswerType(), em);
        } else {
            ResultDAO resultDAO = new ResultDAO();

            resultDAO.removeTagFromAnswer(answerId, tagId, em);
        }

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to create a Tag
     *
     * @param tag     the Text that should be converted to a Tag
     * @param request the HttpRequest
     * @return Status 200 if succeeded | 502 if failed
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Path("/addTag")
    @POST
    @Produces("text/plain")
    public Response addTag(@FormParam("tag") String tag, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        tag = prepareString(tag, DataTypeInfo.TAG.getMaxLength(), true, false);
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        TagDAO tagDAO = new TagDAO();
        Tag tagToAdd = tagDAO.selectByTag(tag, em);
        Tag addedTag;
        if (tagToAdd == null) {
            addedTag = tagDAO.insert(new Tag(tag));
        } else {
            // TODO! Set status to something like "tag already exists!
            return Response.status(409, "Tag existiert bereits!").entity("noTags").build();
        }

        em.getTransaction().commit();
        em.close();

        Get get = new Get();
        return get.getSingleTag(addedTag.getTagID());
    }

    /**
     * Service to edit a Tag
     *
     * @param tagIDs     the ID of the Tag which is going to be edited
     * @param tagContent the new Content of the Tag
     * @param request    the HttpRequest
     * @return Status 200 if succeeded | 502 if failed
     * @author Magnus
     * @since 17.07.2021
     */
    @Path("/editTag") // worked on 21.05.2021 11:37
    @POST
    @Produces("text/plain")
    public Response editTag(@FormParam("tagID") String tagIDs, @FormParam("tagContent") String tagContent, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;


        int tagID = Integer.parseInt(tagIDs);

        TagDAO tagDAO = new TagDAO();
        if (tagContent != null && !tagContent.equals("")) {
            EntityManager em = Connector.getInstance().open();
            em.getTransaction().begin();

            Tag tag = tagDAO.select(tagID, em);
            tag.setTag(prepareString(tagContent, DataTypeInfo.TAG.getMaxLength(), true, false));

            tagDAO.merge(tag, em);

            em.getTransaction().commit();
            em.close();
        }

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to delete a tag
     *
     * @param tag     the ID of the Tag to delete
     * @param request the HttpRequest
     * @return Status 200 if succeeded | 502 if failed
     * @author Magnus
     * @since 17.07.2021
     */
    @Path("/deleteTag") // worked on 21.05.2021 11:39
    @POST
    @Produces("text/plain")
    public Response deleteTag(@FormParam("tagID") String tag, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        int tagId = Integer.parseInt(tag);
        TagDAO tagDAO = new TagDAO();
        tagDAO.remove(tagId);

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to delete an Answer from the Database.
     *
     * @param answerID the ID of the Answer which should be deleted
     * @param request  the HttpRequest
     * @return Status 200 if succeeded | 502 if failed
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Path("/deleteAnswer")
    @POST
    @Produces("text/plain")
    public Response deleteAnswer(@FormParam("answerID") long answerID, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();

        try {
            answerDAO.remove(answerID, em);
        } catch (PersistenceException e) {
            e.printStackTrace();

            return Response.status(409, "Antwort konnte nicht gelöscht werden!").entity("Couln't delete answer!").build();
        }

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    @Path("/editAnsweredQuestion")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response editAnsweredQuestion(@FormParam("questionID") long questionID, @FormParam("question") String question, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;


        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestion answeredQuestion = answeredQuestionDAO.select(questionID, em);
        if (answeredQuestion != null) {
            answeredQuestion.setQuestion(prepareString(question, DataTypeInfo.USER_QUESTION_INPUT.getMaxLength(), true, false, false));
        } else {
            return Response.status(403).build();
        }

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * @param questionID
     * @param request
     * @return
     * @author Tim Irmler
     * @since 19.08.2021
     */
    @Path("/deleteAnsweredQuestion")
    @POST
    @Produces("text/plain")
    public Response deleteAnsweredQuestion(@FormParam("questionID") long questionID, @Context HttpServletRequest request) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        answeredQuestionDAO.removeAllReferences(questionID, em);

        answeredQuestionDAO.remove(questionID, em);

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * @param questionID
     * @param request
     * @return
     * @author Tim Irmler
     * @since 19.08.2021
     */
    @Path("/deleteUnansweredQuestion")
    @POST
    @Produces("text/plain")
    public Response deleteUnansweredQuestion(@FormParam("questionID") long questionID, @Context HttpServletRequest request) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        UnansweredQuestionDAO unansweredQuestionDAO = new UnansweredQuestionDAO();
        unansweredQuestionDAO.removeAllReferences(questionID, em);

        unansweredQuestionDAO.remove(questionID, em);

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to change the Password of a UserLogin
     *
     * @param oldPass the Password to change
     * @param newPass the new Password
     * @param request the HttpRequest
     * @return Status 200 if succeeded | 403 if failed
     * @author Marc
     * @author Magnus
     * @since 17.07.2021
     */
    @Path("/changePassword")
    @POST
    @Produces("text/plain")
    public String changePassword(@FormParam("oldPass") String oldPass, @FormParam("newPass") String newPass, @Context HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {

        } else {

            boolean changed = false;

            printDebug("New Password", newPass);

            if (!stringTooLong(newPass, DataTypeInfo.USER_PASSWORD.getMaxLength())) {
                long userID = (long) request.getSession().getAttribute("user");
                UserLoginDAO userLoginDAO = new UserLoginDAO();
                UserLogin user = userLoginDAO.select(userID);

                if (user != null) {

                    if (user.getPassword().equals(SHA256.getHexStringInstant(oldPass))) {
                        userLoginDAO.setPassword(user, newPass);

                        changed = true;
                        request.setAttribute("user", userLoginDAO.select(user.getUserLoginID()));
                    } else {

                    }
                }
            } else {
                return "{\"error\": \"Das Passwort ist zu lange\", \"changed\":\"" + changed + "\"}";
            }
            return "{\"changed\":\"" + changed + "\"}";
        }
        return "Something went wrong";
    }

    /**
     * @param file
     * @param request
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    @Path("/removeFile")
    @POST
    @Produces("text/plain")
    public Response deleteFile(@FormParam("fileID") String file, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        long fileID = Long.parseLong(file);
        UploadFileDAO fileDAO = new UploadFileDAO();
        AnswerDAO answerDAO = new AnswerDAO();
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<Answer> answers = answerDAO.select(em);
        UploadFile f = fileDAO.select(fileID, em);
        for (Answer a : answers) {
            a.removeFile(f);
        }

        em.getTransaction().commit();
        em.close();

        fileDAO.remove(fileID);

        return Response.ok().entity("ok").build();
    }

    /**
     * method to check if a tag already exists in the answer result
     *
     * @param tagToFind the tag we want to check if it already exists or not
     * @param tags      the array in which we want to check
     * @return returns true if the tag already exists, returns false otherwise
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private boolean existsTagAlreadyInArray(String tagToFind, List<Tag> tags) {
        for (Tag tag : tags) {
            if (tag.getTag().equals(tagToFind)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to get all tags from a string.
     * if the tag already exists in the DB, use this tag. if not, create a new one.
     *
     * @param tagsString the string with all the tags
     * @param splitter   defines how the tags are seperated in the string
     * @param em         the entity manager
     * @return returns an array of all tags
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private ArrayList<Tag> getTagsFromString(String tagsString, String splitter, EntityManager em) {
        TagDAO tagDAO = new TagDAO();
        ArrayList<Tag> tags = new ArrayList<>();
        String[] tagsSplitted = tagsString.split(splitter);
        for (String s : tagsSplitted) { // Go through all tag strings
            s = prepareString(s, DataTypeInfo.TAG.getMaxLength(), true, false);
            if (!existsTagAlreadyInArray(s, tags)) {
                Tag dbTag = tagDAO.selectByTag(s, em);
                if (dbTag != null) {
                    tags.add(dbTag); // if tag already exists in DB, use this tag
                } else {
                    tags.add(new Tag(s)); // create new tag
                }
            }
        }
        return tags;
    }

    /**
     * method to add tag to type tags if tag doesn't already exist with this type
     *
     * @param type       the type of the tag
     * @param tag        the tag
     * @param typeTags   all the type tags to check
     * @param typeTagDAO the type tag dao
     * @param em         the entity manager
     * @author Tim Irmler
     * @since 17.07.2021
     */
    private void addTypeTag(AnswerType type, Tag tag, List<TypeTag> typeTags, TypeTagDAO typeTagDAO, EntityManager em) {
        boolean needToAddTag = true;
        if (tag.getTagID() >= 1) { // if the tag already exists in the DB, check if it already exists with this type
            for (TypeTag typeTag : typeTags) { // go through all tags with this type
                if (tag.getTagID() == typeTag.getTag().getTagID()) { // if this tag with this type already exists, break and dont add tag
                    needToAddTag = false;
                    break;
                }
            }
        }
        // Add tag with type to DB if necessary
        if (needToAddTag) {
            typeTagDAO.insert(new TypeTag(tag, type), em);
        }
    }

    /**
     * Service to add a defaultquestion to the Database
     *
     * @param defaultQuestionString the defaultquestion
     * @param request               the http request
     * @return status 200
     * @author Sarah
     * @since 17.07.2021
     */
    @Path("/addQuestion")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response addQuestion(@FormParam("defaultQuestion") String defaultQuestionString, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;


        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();
        // TODO: 25.07.2021 check if the default question already exists in db 
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();
        DefaultQuestion defaultQuestion = new DefaultQuestion(prepareString(defaultQuestionString, DataTypeInfo.USER_QUESTION_INPUT.getMaxLength(), false, false));

        defaultQuestionDAO.insert(defaultQuestion, em);

        em.getTransaction().commit();
        em.close();

        Get get = new Get();
        return get.getSingleDefaultQuestion(defaultQuestion.getDefaultQuestionID());
    }

    /**
     * Service to edit a default question
     *
     * @param defaultQuestionId the ID of the default question
     * @param defaultQuestion   the default question
     * @param request           the HTTP request
     * @return status
     * @author Sarah
     * @since 17.07.2021
     */
    @Path("/editQuestion")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response editQuestion(@FormParam("defaultQuestionId") long defaultQuestionId, @FormParam("defaultQuestion") String defaultQuestion, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();

        defaultQuestionDAO.update(defaultQuestionId, prepareString(defaultQuestion, DataTypeInfo.USER_QUESTION_INPUT.getMaxLength(), false, false));

        em.getTransaction().commit();
        em.close();

        return Response.ok().entity("ok").build();
    }

    /**
     * Service to delete defaultQuestion
     *
     * @param questionId the id of the defaultQuestion to delete
     * @param request    the HttpRequest
     * @return Status 200 if succeeded | 502 if failed
     * @author Sarah Ambi
     * @since 25.08.2021
     */
    @Path("/deleteQuestion")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteQuestion(@FormParam("questionId") String questionId, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        String out = "";

        int id = Integer.parseInt(questionId);
        DefaultQuestionDAO defaultQuestionDAO = new DefaultQuestionDAO();

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        Long amountQuestions = defaultQuestionDAO.count(em);
        if (amountQuestions > 3) {
            defaultQuestionDAO.remove(id, em);
            out = "success";

            em.getTransaction().commit();
            em.close();
            return Response.status(200).entity(out).build();
        } else {
            out = "not success";

            em.getTransaction().commit();
            em.close();
            return Response.status(400).entity(out).build();
        }
    }

    /**
     * Service to edit a Blacklist entry
     *
     * @param entryId the id of the Entry which is going to be edited
     * @param content the new content for the BlacklistEntry
     * @param request
     * @return if it worked
     * @author Marc Andri Fuchs
     * @see BlacklistEntry
     */
    @Path("/editBlacklistEntry")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response editBlacklistEntry(@FormParam("entryId") String entryId, @FormParam("content") String content, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        long id;
        try {
            id = Long.parseLong(entryId);
        } catch (NumberFormatException e) {
            return Response.status(400).entity("the provided id is not a valid number").build();
        }

        BlacklistEntryDAO blDAO = new BlacklistEntryDAO();
        BlacklistEntry bl = blDAO.select(id);

        bl.setWord(prepareString(content, DataTypeInfo.BLACK_LIST_ENTRY.getMaxLength(), true, false));

        blDAO.merge(bl);

        return Response.status(200).entity("success!").build();
    }

    /**
     * Service to add a BlacklistEntry to the Database
     *
     * @param content the Word which will be blacklisted
     * @param request
     * @return the newly created BlacklistEntry
     * @author Marc Andri Fuchs
     * @author Tim Irmler
     * @see BlacklistEntry
     * @since 22.07.2021
     */
    @Path("/addBlacklistEntry")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response addBlacklistEntry(@FormParam("content") String content, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        BlacklistEntryDAO dao = new BlacklistEntryDAO();

        BlacklistEntry blEntry = new BlacklistEntry(prepareString(content, DataTypeInfo.BLACK_LIST_ENTRY.getMaxLength(), true, false));

        // TODO: 25.07.2021 check if the blacklist entry already exists

        dao.insert(blEntry);

        Get get = new Get();
        return get.getSingleBlacklistEntry(blEntry.getBlackListID());
    }

    /**
     * Service to delete a BlacklistEntry from the Database
     *
     * @param entryId the ID of the BlacklistEntry
     * @param request
     * @return if it worked
     * @author Marc Andri Fuchs
     * @see BlacklistEntry
     */
    @Path("/deleteBlacklistEntry")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteBlacklistEntry(@FormParam("entryId") String entryId, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        long id;
        try {
            id = Long.parseLong(entryId);
        } catch (NumberFormatException e) {
            return Response.status(400).entity("the provided id is not a valid number").build();
        }

        BlacklistEntryDAO entryDAO = new BlacklistEntryDAO();
        entryDAO.remove(id);

        return Response.status(200).entity("success!").build();
    }

    /**
     * Method to check if the user is logged in
     *
     * @param request the Request of the User
     * @return null if the user is logged in, else a Response with the HTTP Code 403
     */
    private Response userIsLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("user") == null ? Response.status(403).build() : null;
    }

    /**
     * Service to check if a User is logged in
     *
     * @param request the request of the User
     * @return a json object containing if the User is logged in
     */
    @Path("/checkLogin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkLogin(@Context HttpServletRequest request) {
        return Response.status(200).entity("{ \"isLoggedIn\": " + (request.getSession().getAttribute("user") != null) + " }").build();
    }

    /**
     * Service to blacklist a word from a Match
     *
     * @param sId     the Id of the Match
     * @param request the request of the User
     * @return a json Object if it worked
     * @see BlacklistEntry
     * @see Match
     */
    @Path("/blacklistMatch")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response blacklistMatch(@FormParam("matchId") String sId, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        try {
            EntityManager em = Connector.getInstance().open();
            em.getTransaction().begin();
            MatchDAO matchDAO = new MatchDAO();
            BlacklistEntryDAO blDAO = new BlacklistEntryDAO();

            Match match = matchDAO.select(Long.parseLong(sId), em); // Get Match from Database
            BlacklistEntry newEntry = new BlacklistEntry(match.getWord()); // Create BlacklistEntry of matched word

            matchDAO.remove(match, em); // remove Match
            blDAO.insert(newEntry, em); // insert BlacklistEntry to Database

            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{ \"success\": false }").build();
        }

        return Response.status(200).entity("{ \"success\": true }").build();
    }

    @Path("resetMatchRating")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetMatchRating(@FormParam("matchID") long id, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        MatchDAO matchDAO = new MatchDAO();
        Match match = matchDAO.select(id, em);
        if (match == null) {
            em.getTransaction().commit();
            em.close();
            return Response.status(500).entity("{ \"success\": false }").build();
        }

        try {
            match = matchDAO.resetRatings(id, em);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{ \"success\": false }").build();
        } finally {
            em.getTransaction().commit();
            em.close();
        }
        String status = CalculateRating.getMatchStatus(match);

        String returnString = "{ \"success\": " + true + ", \"status\": \"" + status + "\" }";
        printDebug("returnString resetRating", returnString);
        return Response.status(200).entity(returnString).build();
    }

    /**
     * Service to delete a UserLogin from the Database
     *
     * @param userId  the ID of the UserLogin
     * @param request the HTTP request
     * @return Status 200 if succeeded | 502 if failed
     * @author Sarah Ambi
     * @since 02.08.2021
     */
    @Path("/deleteUser")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(@FormParam("userId") long userId, @Context HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null)
            return Response.status(403).build();

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        UserLoginDAO userLoginDAO = new UserLoginDAO();
        try {
            userLoginDAO.remove(userId, em);
        } catch (NumberFormatException e) {
            em.getTransaction().commit();
            em.close();
            return Response.status(400).entity("the provided id is not a valid number").build();
        }

        em.getTransaction().commit();
        em.close();

        return Response.status(200).entity("success!").build();
    }

    /**
     * Service to edit user
     *
     * @param userId        the ID of the user
     * @param email         the eamil of the user
     * @param canCreateUser if it can create user
     * @param request       the HTTP request
     * @return status
     * @author Sarah Ambi
     * @since 03.08.2021
     */
    @Path("/editUser")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response editUser(@FormParam("userId") long userId, @FormParam("email") String email, @FormParam("canCreateUser") int canCreateUser, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) {
            return userResponse;
        } else {
            UserLoginDAO userLoginDAO = new UserLoginDAO();
            UserLogin user = userLoginDAO.select((long) request.getSession().getAttribute("user"));
            if (!user.isCanCreateUsers()) {
                return Response.status(403).build();
            }
        }

        printDebug("CAN CREATE USER IN SERVICE", canCreateUser);
        UserLoginDAO userLoginDAO = new UserLoginDAO();

        UserLogin userLogin;
        if (canCreateUser > 0) {
            boolean canCreateUserToBoolean = (canCreateUser == 1); // 1 = true, 2 = false

            userLogin = new UserLogin(userId, prepareString(email, DataTypeInfo.USER_EMAIL.getMaxLength(), false, false), canCreateUserToBoolean);
        } else {
            userLogin = new UserLogin(userId, prepareString(email, DataTypeInfo.USER_EMAIL.getMaxLength(), false, false));
        }

        userLoginDAO.update(userLogin);

        return Response.ok().entity("ok").build();
    }

    /**
     * @param matchID
     * @param request
     * @return
     * @author Tim Irmler
     * @since 14.08.2021
     */
    @Path("/noTranslate")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response noTranslate(@FormParam("matchID") long matchID, @Context HttpServletRequest request) {
        Response userResponse = userIsLoggedIn(request);
        if (userResponse != null) return userResponse;

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        MatchDAO matchDAO = new MatchDAO();
        Match match = matchDAO.select(matchID, em);
        int minDownvotes = CalculateRating.minDownvotesForNoTranslate;
        if (match != null) {
            match.setUpvote(0);
            match.setDownvote(minDownvotes);
        } else {
            em.getTransaction().commit();
            em.close();
            return Response.status(500).entity("{ \"success\": false }").build();
        }
        float newRating = CalculateRating.getRating(match.getUpvote(), match.getDownvote());
        String status = CalculateRating.getMatchStatus(match);

        em.getTransaction().commit();
        em.close();

        String returnString = "{\"success\": true, \"downvotes\": " + minDownvotes + ", \"rating\": " + newRating + ", \"status\": \"" + status + "\" }";
        printDebug("returnString noTranslate", returnString);
        return Response.status(200).entity(returnString).build();
    }
}