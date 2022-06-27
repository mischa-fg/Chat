package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.StatistikDay;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class StatistikDayDAO extends DAO<StatistikDay> {

    public StatistikDayDAO() {
        super(StatistikDay.class);
    }

    public StatistikDay selectByDate(Date date, EntityManager em) {
        try {
            return em.createQuery("select d from StatistikDay d where d.myDate = :date", StatistikDay.class).setParameter("date", StatistikDay.getSimpleDateFormatStatic(date)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StatistikDay selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();
        StatistikDay out = null;

        try {
            if (!em.getTransaction().isActive())
                em.getTransaction().begin();

             out = selectByDate(date, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return out;
    }
}