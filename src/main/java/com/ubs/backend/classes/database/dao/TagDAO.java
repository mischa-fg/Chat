package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.TempTag;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.Tag;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * DAO for com.ubs.backend.classes.database.Tag
 */
public class TagDAO extends DAO<Tag> {
    public TagDAO() {
        super(Tag.class);
    }

    public Tag selectByTag(String tag, EntityManager em) {
        try {
            return em.createQuery("select t from Tag t where lower(t.tag) = lower(:tag)", Tag.class).setParameter("tag", tag).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * select tag by tag content
     *
     * @param tag
     *
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public Tag selectByTag(String tag) {
        EntityManager em = Connector.getInstance().open();
        Tag out = null;

        try {
            em.getTransaction().begin();

            out = selectByTag(tag, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return out;
    }

    /**
     * select all tags that do not have any connection to any kind of answers
     *
     * @param em
     *
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<Tag> selectNoAnswerTag(EntityManager em) {
        try {
            return em.createQuery("select tag from Tag tag where not exists (select tagR.tag.tagID from Result tagR where tag.tagID = tagR.tag.tagID) AND NOT EXISTS (select tagT.tag.tagID from TypeTag tagT where tag.tagID = tagT.tag.tagID)",
                    Tag.class).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Removes Tag from the Database
     *
     * @param id the ID of the Tag
     */
    public void remove(long id) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            ResultDAO resultDAO = new ResultDAO();
            resultDAO.removeTag(id, em);

            TypeTagDAO typeTagDAO = new TypeTagDAO();
            typeTagDAO.removeTag(id, em);

            MatchDAO matchDAO = new MatchDAO();
            matchDAO.removeByTag(id, em);

            em.createQuery("delete from Tag where tagID = :id").setParameter("id", id).executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param tagID
     * @param em
     *
     * @return
     * @author Sarah Ambi
     * @since 27.07.2021
     */
    public TempTag getSingleTempTag(long tagID,EntityManager em) {
        Tag tag = em.createQuery("SELECT t FROM Tag t WHERE t.tagID = :id", Tag.class).setParameter("id", tagID).getSingleResult();
        return new TempTag(tag);
    }

    /**
     * Selects a single temp tag with id
     * @param tagID
     *
     * @return the new generated tempTag
     * @author Sarah Ambi
     * @since 27.07.2021
     */
    public TempTag getSingleTempTag(long tagID) {
        EntityManager em = Connector.getInstance().open();

        em.getTransaction().begin();

        TempTag tempTag = getSingleTempTag(tagID, em);

        em.getTransaction().commit();
        em.close();

        return tempTag;
    }
}