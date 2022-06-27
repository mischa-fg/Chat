package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.StatistikYear;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

public class StatistikYearDAO extends DAO<StatistikYear> {
    public StatistikYearDAO() {
        super(StatistikYear.class);
    }

    public StatistikYear selectByDate(Date date, EntityManager em) {
        try {
            return em.createQuery("select y from StatistikYear y where y.myDate = :date", StatistikYear.class).setParameter("date", StatistikYear.getSimpleDateFormatStatic(date)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StatistikYear selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();
        StatistikYear out = null;

        try {
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
