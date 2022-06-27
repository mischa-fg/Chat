package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.BlacklistEntry;
import com.ubs.backend.classes.database.Connector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * DAO for com.ubs.backend.classes.database.BlacklistEntry
 */
public class BlacklistEntryDAO extends DAO<BlacklistEntry> {
    public BlacklistEntryDAO() {
        super(BlacklistEntry.class);
    }

    /**
     * @param word
     * @return
     * @author Tim Irmler
     * @since 19.07.2021
     */
    public BlacklistEntry selectByWord(String word, EntityManager em) {
        try {
            return em.createQuery("select b from BlacklistEntry b where lower(b.word) = lower(:word)", BlacklistEntry.class).setParameter("word", word).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void merge(BlacklistEntry bl) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        em.merge(bl);

        em.getTransaction().commit();
        em.close();
    }
}