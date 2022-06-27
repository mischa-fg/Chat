package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.UploadFile;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * DAO for com.ubs.backend.classes.database.UploadFile
 */
public class UploadFileDAO extends DAO<UploadFile> {
    public UploadFileDAO() {
        super(UploadFile.class);
    }

    /**
     * Gets the amount of UploadFiles in the Database
     *
     * @param id the ID of the UploadFile
     * @return the amount of UploadFiles with this ID
     */
    public long getFileCount(long id) {
        EntityManager em = Connector.getInstance().open();
        long count = 0;

        try {
            em.getTransaction().begin();

            count = em.createQuery("select count(f) from UploadFile f where f.fileID = :id", Long.class).setParameter("id", id).getSingleResult();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return count;
    }

    public List<String> getFileNames() {
        EntityManager em = Connector.getInstance().open();
        List<String> out = null;

        try {
            em.getTransaction().begin();

            out = em.createQuery("select f.fileName from UploadFile f", String.class).getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return out;
    }
}
