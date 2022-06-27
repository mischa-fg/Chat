package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.TypeTag;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionDAO;
import com.ubs.backend.classes.database.dao.questions.AnsweredQuestionTimesResultDAO;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class TypeTagDAO extends DAO<TypeTag> {

    public TypeTagDAO() {
        super(TypeTag.class);
    }

    /**
     * @param type
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<TypeTag> selectByType(AnswerType type) {
        EntityManager em = Connector.getInstance().open();
        List<TypeTag> typeTags = null;

        try {
            em.getTransaction().begin();

            typeTags = selectByType(type, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return typeTags;
    }

    /**
     * select all typedTags by their type
     *
     * @param type the type of the tags that we want
     * @param em   the Entity Manager
     * @return list containing all found typedTags
     */
    public List<TypeTag> selectByType(AnswerType type, EntityManager em) {
        try {
            
            
            return em.createQuery("Select tt from TypeTag tt where tt.answerType = :type", TypeTag.class).setParameter("type", type).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param type
     * @param max
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<TypeTag> selectByType(AnswerType type, int max, EntityManager em) {
        try {
            if (max < 0) {
                return selectByType(type, em);
            }
            return em.createQuery("Select tt from TypeTag tt where tt.answerType = :type", TypeTag.class).setParameter("type", type).setMaxResults(max).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param type
     * @param tagName
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TypeTag selectByTagNameAndType(AnswerType type, String tagName) {
        EntityManager em = Connector.getInstance().open();
        TypeTag typeTag = null;

        try {
            em.getTransaction().begin();

            typeTag = selectByTagNameAndType(type, tagName, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return typeTag;
    }

    /**
     * @param type
     * @param tagName
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TypeTag selectByTagNameAndType(AnswerType type, String tagName, EntityManager em) {
        try {
            return em.createQuery("select tt from TypeTag tt where lower(tt.tag.tag) = lower(:tagName) and tt.answerType = :answerType", TypeTag.class).setParameter("tagName", tagName).setParameter("answerType", type).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param type
     * @param tagId
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public TypeTag selectByTagAndType(AnswerType type, long tagId, EntityManager em) {
        try {
            return em.createQuery("select tt from TypeTag tt where tt.tag.tagID = :tagId and tt.answerType = :type", TypeTag.class).setParameter("tagId", tagId).setParameter("type", type).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param tagId
     * @param em
     * @return
     * @author Tim Irmler
     * @since 27.07.2021
     */
    public List<TypeTag> selectByTag(long tagId, EntityManager em) {
        try {
            return em.createQuery("select tt from TypeTag tt where tt.tag.tagID = :id", TypeTag.class).setParameter("id", tagId).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param type
     * @param tagID
     * @param isUpvote
     * @param revert
     * @author Tim Irmler
     * @since 21.07.2021
     */
    public void vote(AnswerType type, long tagID, boolean isUpvote, boolean revert, String question, EntityManager em) {
        try {
            TypeTag tt = selectByTagAndType(type, tagID, em);

            if (tt != null) {
                if (!tt.getAnswerType().isHidden()) {
                    AnsweredQuestionTimesResultDAO answeredQuestionTimesResultDAO = new AnsweredQuestionTimesResultDAO();
                    answeredQuestionTimesResultDAO.vote(tt, isUpvote, revert, question, em);
                }

                if (isUpvote) {
                    tt.setUpvotes(tt.getUpvotes() + 1);
                    if (revert) {
                        tt.setDownvotes(tt.getDownvotes() - 1);
                    }
                } else {
                    tt.setDownvotes(tt.getDownvotes() + 1);
                    if (revert) {
                        tt.setUpvotes(tt.getUpvotes() - 1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeByTagIdAndType(long tagId, AnswerType type) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            removeByTagIdAndType(tagId, type, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void removeByTagIdAndType(long tagId, AnswerType type, EntityManager em) {
        removeReferences(tagId, type, em);

        em.createQuery("delete from TypeTag t where t.tag.tagID = :id and t.answerType = :type").setParameter("id", tagId).setParameter("type", type).executeUpdate();
    }

    public void removeTag(long id, EntityManager em) {
        removeReferences(id, em);

        em.createQuery("delete from TypeTag t where t.tag.tagID = :id").setParameter("id", id).executeUpdate();
    }

    public void removeReferences(long tagId, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        answeredQuestionDAO.removeResultByTag(tagId, em);
    }

    public void removeReferences(long tagId, AnswerType answerType, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        answeredQuestionDAO.removeResultByTagAndType(tagId, answerType, em);
    }
}
